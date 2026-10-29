/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2026 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.poll.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.meeds.poll.utils.PollUtils;

import tools.jackson.databind.ObjectMapper;

/**
 * The collaborative activity declaration analytics reads from this jar: a
 * vote counts; creating a poll counts through the post it creates, so it is
 * not declared (D11). The strings are read from PollUtils, where the listener
 * takes them; the label keys are matched by hand against poll-webapp's
 * Portlets_en.properties.
 */
class CollaborativeActivityDeclarationTest {

  private static final String DECLARATION_FILE = "analytics-collaborative-activities.json";

  @Test
  @SuppressWarnings("unchecked")
  void theDeclarationCountsTheVoteAndNotThePollCreation() throws IOException {
    Map<String, Object> declaration;
    try (InputStream content = getClass().getClassLoader().getResourceAsStream(DECLARATION_FILE)) {
      assertNotNull(content, "the declaration ships at the root of poll-services, which lands in /lib");
      declaration = new ObjectMapper().readValue(content, Map.class);
    }

    assertEquals(List.of(PollUtils.POLL_MODULE), declaration.get("modules"));
    assertEquals(List.of(PollUtils.VOTE_POLL_OPERATION_NAME), declaration.get("operations"));
    assertFalse(((List<String>) declaration.get("operations")).contains(PollUtils.CREATE_POLL_OPERATION_NAME),
                "the post a poll creates carries the count");
    assertFalse(declaration.containsKey("legacyOperations"));
    assertFalse(declaration.containsKey("excludedActivityTypes"));
    assertFalse(declaration.containsKey("browserOperations"));

    Map<String, Object> labels = (Map<String, Object>) declaration.get("labels");
    assertEquals(List.of("poll.collaborativeActivity.help.counted.vote"), labels.get("counted"));
  }

}
