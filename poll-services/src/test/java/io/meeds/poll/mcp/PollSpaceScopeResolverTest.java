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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.meeds.poll.service.PollService;

/**
 * Pins which space a poll belongs to for the space scope decision.
 */
class PollSpaceScopeResolverTest {

  private PollService            pollService;

  private PollSpaceScopeResolver resolver;

  /**
   * Creates the resolver over a mocked service.
   */
  @BeforeEach
  void setUp() {
    pollService = Mockito.mock(PollService.class);
    resolver = new PollSpaceScopeResolver(pollService);
  }

  /**
   * A poll posted in a space belongs to that space, and the resolver answers
   * to the name the tool definitions give. Mutants: the space id not read;
   * the name changed.
   */
  @Test
  void pollOfASpaceBelongsToThatSpace() {
    when(pollService.getPollSpaceId(7L)).thenReturn(12L);

    assertEquals("poll", resolver.getName());
    assertEquals(PollSpaceScopeResolver.NAME, resolver.getName());
    assertEquals(12L, resolver.spaceOf("7"));
  }

  /**
   * A poll of no space (id 0) belongs to none: the call's space can't match
   * it. Mutant: the {@code spaceId > 0} check removed.
   */
  @Test
  void pollOfNoSpaceBelongsToNone() {
    when(pollService.getPollSpaceId(7L)).thenReturn(0L);

    assertNull(resolver.spaceOf("7"));
  }

  /**
   * A missing poll (the service answers 0) belongs to no space.
   */
  @Test
  void missingPollBelongsToNone() {
    when(pollService.getPollSpaceId(8L)).thenReturn(0L);

    assertNull(resolver.spaceOf("8"));
  }

  /**
   * An id that isn't a number is never looked up. Mutant: the numeric guard
   * removed (the lookup would then be attempted or the parse would throw).
   */
  @Test
  void nonNumericIdIsNeverLookedUp() {
    assertNull(resolver.spaceOf("abc"));
    assertNull(resolver.spaceOf("7 "));
    assertNull(resolver.spaceOf("-7"));
    assertNull(resolver.spaceOf(""));
    assertNull(resolver.spaceOf(null));
    verify(pollService, never()).getPollSpaceId(anyLong());
  }

  /**
   * A failing lookup, and an id too large for a long, belong to no space and
   * never throw. Mutant: the catch removed.
   */
  @Test
  void failingLookupBelongsToNone() {
    when(pollService.getPollSpaceId(9L)).thenThrow(new IllegalStateException("database down"));

    assertNull(resolver.spaceOf("9"));
    assertNull(resolver.spaceOf("99999999999999999999999"));
  }

}
