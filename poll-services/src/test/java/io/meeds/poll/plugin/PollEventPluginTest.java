/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2022 Meeds Association contact@meeds.io
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
package io.meeds.poll.plugin;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { PollEventPlugin.class, })
class PollEventPluginTest {

  @Test
  void isValidPollEvent() {
    // When
    PollEventPlugin pollEventPlugin = new PollEventPlugin();
    Map<String, String> eventProperties = new HashMap<>();
    eventProperties.put("activityId", "1");
    String triggerDetails = "{spaceId: 1, activityId: 2}";

    // Then
    assertFalse(pollEventPlugin.isValidEvent(eventProperties, triggerDetails));

    // When
    eventProperties = new HashMap<>();
    eventProperties.put("activityId", "2");
    triggerDetails = "{spaceId: 1, activityId: 2}";

    // Then
    assertTrue(pollEventPlugin.isValidEvent(eventProperties, triggerDetails));

    // When
    eventProperties = new HashMap<>();
    eventProperties.put("spaceId", "1");
    triggerDetails = "{spaceId: 1, activityId: 2}";

    // Then
    assertTrue(pollEventPlugin.isValidEvent(eventProperties, triggerDetails));
  }

}
