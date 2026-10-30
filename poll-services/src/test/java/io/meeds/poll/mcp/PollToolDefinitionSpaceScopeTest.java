/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2026 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.poll.mcp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import io.meeds.mcp.server.constant.Origin;
import io.meeds.mcp.server.model.ActingIdentity;
import io.meeds.mcp.server.model.SimpleToolDefinition;
import io.meeds.mcp.server.model.SpaceScopeDeclaration;
import io.meeds.mcp.server.model.SpaceScopeRefusedException;
import io.meeds.mcp.server.plugin.SpaceScopeResolver;
import io.meeds.mcp.server.service.McpToolSpaceScopeService;
import io.meeds.mcp.server.util.McpToolUtils;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Pins the {@code space_scope} declarations this module ships: which poll
 * tools a call limited to one space may use, and on which arguments.
 */
class PollToolDefinitionSpaceScopeTest {

  private static final String       DEFINITIONS      = "ai-tool-definitions.json";

  private static final Set<String>  SPACE_READS      = Set.of("list_space_polls");

  private static final Set<String>  OBJECT_READS     = Set.of("get_poll_results");

  private static final Set<String>  SPACE_WRITES     = Set.of("create_poll");

  /**
   * An argument shaped like a reference to a place or an object: an id or
   * ids, in snake or camel case, a folder, a space.
   */
  private static final Pattern      REFERENCE_SHAPED = Pattern.compile("(^|_)ids?$|[a-z]Ids?$|(?i:folder|space)");

  private static final JsonMapper   MAPPER           = JsonMapper.builder().build();

  /** The role each space-scoped write declares in {@code space_role}. */
  private static final Map<String, String> SPACE_ROLES = Map.of("create_poll", "redactor");

  /**
   * Exactly the expected tools declare a space scope, each of the expected
   * kind: {@code list_space_polls} fills in the call's space, {@code
   * create_poll} is a write naming its space, {@code get_poll_results}
   * resolves the poll's space. Mutants: a declaration dropped; {@code
   * when_absent} dropped; the resolver renamed.
   */
  @Test
  void declaredToolsAreTheExpectedOnes() {
    Map<String, SimpleToolDefinition> declared = declaredTools();

    assertEquals(Set.of("create_poll", "get_poll_results", "list_space_polls"), declared.keySet());
    declared.forEach((name, tool) -> {
      boolean write = SPACE_WRITES.contains(name);
      assertNotNull(tool.getAnnotations(), name);
      assertEquals(!write, McpToolSpaceScopeService.isReadTool(tool), name);
      assertTrue(tool.getSpaceScope().isWellFormedFor(write), name);
    });
    SPACE_READS.forEach(name -> {
      SpaceScopeDeclaration scope = declared.get(name).getSpaceScope();
      assertTrue(scope.isSpaceKind(), name);
      assertEquals("space_id", scope.getSpaceArg(), name);
      assertTrue(scope.isUseScopeWhenAbsent(), name);
    });
    SPACE_WRITES.forEach(name -> {
      SpaceScopeDeclaration scope = declared.get(name).getSpaceScope();
      assertTrue(scope.isSpaceKind(), name);
      assertEquals("space_id", scope.getSpaceArg(), name);
      assertNull(scope.getWhenAbsent(), name);
    });
    OBJECT_READS.forEach(name -> {
      SpaceScopeDeclaration scope = declared.get(name).getSpaceScope();
      assertTrue(scope.isObjectKind(), name);
      assertEquals("poll_id", scope.getObjectArg(), name);
      assertEquals(PollSpaceScopeResolver.NAME, scope.getResolver(), name);
    });
  }

  /**
   * A read accepts exactly the tool's schema properties: none it doesn't
   * take, none it takes left unchecked. A write accepts only schema
   * properties, and leaves out only reference-shaped ones. Mutants: an
   * argument dropped from {@code list_space_polls}'s allowed list; a
   * property no schema holds added.
   */
  @Test
  void acceptedArgumentsAreTheSchemaProperties() {
    declaredTools().forEach((name, tool) -> {
      Set<String> schema = new LinkedHashSet<>(properties(tool).propertyNames());
      Set<String> accepted = acceptedArguments(tool.getSpaceScope());
      if (McpToolSpaceScopeService.isReadTool(tool)) {
        assertEquals(schema, accepted, name);
      } else {
        assertTrue(schema.containsAll(accepted), name);
        schema.stream()
              .filter(argument -> !accepted.contains(argument))
              .forEach(argument -> assertTrue(isReferenceShaped(argument), name + " leaves out " + argument));
      }
    });
  }

  /**
   * No write lets a call limited to a space pass a reference to another place
   * or object: besides its space argument, {@code create_poll} accepts
   * nothing shaped like an id, a folder or a space. Mutant: {@code space_id}
   * listed among the allowed arguments of {@code create_poll}.
   */
  @Test
  void writesAcceptNoReferenceBesidesTheirPlace() {
    declaredTools().forEach((name, tool) -> {
      if (McpToolSpaceScopeService.isReadTool(tool)) {
        return;
      }
      SpaceScopeDeclaration scope = tool.getSpaceScope();
      List<String> others = scope.getAllowedArgs() == null ? List.of() : scope.getAllowedArgs();
      others.forEach(argument -> assertFalse(isReferenceShaped(argument), name + " accepts " + argument));
    });
    for (String reference : List.of("id", "ids", "poll_id", "spaceId", "folderId", "Space", "space_id")) {
      assertTrue(isReferenceShaped(reference), reference);
    }
    for (String plain : List.of("question", "options", "duration", "message", "limit")) {
      assertFalse(isReferenceShaped(plain), plain);
    }
  }

  /**
   * {@code vote_in_poll} stays undeclared, so a call limited to a space never
   * runs it: whether a digital worker may vote on behalf of a user is an open
   * product question, and until it is settled the tool fails closed. Mutant:
   * a space scope declared on {@code vote_in_poll}.
   */
  @Test
  void voteInPollStaysUndeclaredUntilTheProductDecidesWhetherAnAgentMayVote() {
    List<SimpleToolDefinition> shipped = McpToolUtils.parseToolDefinitions(definitionsUrl());

    SimpleToolDefinition vote = shipped.stream()
                                       .filter(tool -> "vote_in_poll".equals(tool.getName()))
                                       .findFirst()
                                       .orElseThrow();
    assertNull(vote.getSpaceScope());
  }

  /**
   * The shipped definitions, run through the server's check with a mocked
   * poll resolver: a poll of the call's space passes, a poll of another
   * space is refused, {@code list_space_polls} without a space is filled with
   * the call's space, {@code create_poll} in another space is refused, and
   * the undeclared {@code vote_in_poll} is refused. Mutants: the resolver
   * name changed; the declaration dropped.
   */
  @Test
  @SuppressWarnings("unchecked")
  void shippedDeclarationsAreEnforcedUnderASpaceScope() {
    SpaceScopeResolver resolver = mock(SpaceScopeResolver.class);
    when(resolver.getName()).thenReturn(PollSpaceScopeResolver.NAME);
    when(resolver.spaceOf("7")).thenReturn(12L);
    when(resolver.spaceOf("8")).thenReturn(99L);
    ObjectProvider<SpaceScopeResolver> provider = mock(ObjectProvider.class);
    when(provider.orderedStream()).thenAnswer(invocation -> Stream.of(resolver));
    McpToolSpaceScopeService service = new McpToolSpaceScopeService(provider);
    ActingIdentity read = ActingIdentity.agent("agent-poll", "POLL_ASSISTANT", Origin.MENTION, 12L);
    ActingIdentity write = ActingIdentity.agent("agent-poll", "POLL_ASSISTANT", Origin.PROPOSAL, 12L);
    Map<String, SimpleToolDefinition> shipped = McpToolUtils.parseToolDefinitions(definitionsUrl())
                                                            .stream()
                                                            .collect(Collectors.toMap(SimpleToolDefinition::getName, tool -> tool));

    String inSpace = "{\"poll_id\":7}";
    assertEquals(inSpace, service.enforce("get_poll_results", shipped.get("get_poll_results"), inSpace, read));
    String elsewhere = "{\"poll_id\":8}";
    assertThrows(SpaceScopeRefusedException.class,
                 () -> service.enforce("get_poll_results", shipped.get("get_poll_results"), elsewhere, read));

    String filled = service.enforce("list_space_polls", shipped.get("list_space_polls"), "{}", read);
    assertEquals(12, MAPPER.readTree(filled).path("space_id").asInt());
    String otherSpaceList = "{\"space_id\":13}";
    assertThrows(SpaceScopeRefusedException.class,
                 () -> service.enforce("list_space_polls", shipped.get("list_space_polls"), otherSpaceList, read));

    String create = "{\"space_id\":12,\"question\":\"Q\",\"options\":[\"a\",\"b\"],\"duration\":\"1day\"}";
    assertEquals(create, service.enforce("create_poll", shipped.get("create_poll"), create, write));
    String createElsewhere = "{\"space_id\":13,\"question\":\"Q\",\"options\":[\"a\",\"b\"],\"duration\":\"1day\"}";
    assertThrows(SpaceScopeRefusedException.class,
                 () -> service.enforce("create_poll", shipped.get("create_poll"), createElsewhere, write));
    assertThrows(SpaceScopeRefusedException.class,
                 () -> service.enforce("create_poll", shipped.get("create_poll"), create, read));

    String vote = "{\"poll_option_id\":10}";
    assertThrows(SpaceScopeRefusedException.class,
                 () -> service.enforce("vote_in_poll", shipped.get("vote_in_poll"), vote, write));
  }

  /**
   * @param argument an argument name
   * @return true when the argument is shaped like a reference to a place or
   *         an object
   */
  private static boolean isReferenceShaped(String argument) {
    return REFERENCE_SHAPED.matcher(argument).find();
  }

  /**
   * @param tool the tool
   * @return the properties of its input schema
   */
  private static JsonNode properties(SimpleToolDefinition tool) {
    return MAPPER.readTree(tool.getInputSchema()).path("properties");
  }

  /**
   * @param scope a space scope declaration
   * @return the arguments a limited call may carry
   */
  private static Set<String> acceptedArguments(SpaceScopeDeclaration scope) {
    Set<String> accepted = new LinkedHashSet<>();
    if (scope.getSpaceArg() != null) {
      accepted.add(scope.getSpaceArg());
    }
    if (scope.getObjectArg() != null) {
      accepted.add(scope.getObjectArg());
    }
    if (scope.getAllowedArgs() != null) {
      accepted.addAll(scope.getAllowedArgs());
    }
    if (scope.getFalseOnlyArgs() != null) {
      accepted.addAll(scope.getFalseOnlyArgs());
    }
    return accepted;
  }

  /**
   * @return the URL of the shipped tool definitions
   */
  private URL definitionsUrl() {
    return getClass().getClassLoader().getResource(DEFINITIONS);
  }

  /**
   * @return the shipped tools that declare a space scope, by name
   */
  private Map<String, SimpleToolDefinition> declaredTools() {
    return McpToolUtils.parseToolDefinitions(definitionsUrl())
                       .stream()
                       .filter(tool -> tool.getSpaceScope() != null)
                       .collect(Collectors.toMap(SimpleToolDefinition::getName, tool -> tool));
  }

  /**
   * Each space-scoped write declares in {@code space_role} the role the member
   * who asks a digital worker for it must hold in the call's space
   * (EXO-91405), and no space-scoped read declares one: a poll is created by
   * whoever {@code canRedactOnSpace} ({@code PollServiceImpl#createPoll}), so
   * {@code redactor}.
   * Mutants: a write's role dropped or changed, or a read given one.
   *
   * @throws Exception when the shipped definitions can't be read
   */
  @Test
  void spaceScopedWritesDeclareTheAskersSpaceRole() throws Exception {
    Map<String, String> roles = new HashMap<>();
    JsonNode definitions = MAPPER.readTree(definitionsUrl().openStream());
    for (JsonNode tool : definitions.path("tools")) {
      String name = tool.path("name").asString();
      JsonNode scope = tool.path("space_scope");
      if (!scope.isObject()) {
        continue;
      }
      if (tool.path("annotations").path("readOnlyHint").asBoolean(false)) {
        assertFalse(scope.has("space_role"), name);
      } else {
        assertTrue(scope.has("space_role"), name);
        roles.put(name, scope.path("space_role").asString());
        if (scope.path("space_admin").asBoolean(false)) {
          assertEquals("manager", roles.get(name), name);
        }
      }
    }
    assertEquals(SPACE_ROLES, roles);
  }

}
