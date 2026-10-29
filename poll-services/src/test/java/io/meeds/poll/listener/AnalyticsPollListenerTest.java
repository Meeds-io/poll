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
package io.meeds.poll.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.services.listener.Event;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.analytics.model.StatisticData;
import io.meeds.analytics.utils.AnalyticsUtils;
import io.meeds.poll.model.Poll;
import io.meeds.poll.service.PollService;
import io.meeds.poll.utils.PollUtils;

/**
 * A vote is stored with the space of its poll: without the space, a vote
 * would count in the platform-wide figures only and never move the Last
 * Activity of the space.
 */
@ExtendWith(MockitoExtension.class)
class AnalyticsPollListenerTest {

  @Mock
  private IdentityManager       identityManager;

  @Mock
  private SpaceService          spaceService;

  @Mock
  private PollService           pollService;

  @InjectMocks
  private AnalyticsPollListener listener;

  @Test
  void aVoteIsStoredWithTheSpaceOfItsPoll() throws Exception {
    Poll poll = new Poll();
    poll.setId(7L);
    poll.setSpaceId(42L);
    poll.setActivityId(99L);
    poll.setCreatedDate(new Date(1_700_000_000_000L));
    poll.setEndDate(new Date(1_700_000_000_000L + 86_400_000L));
    Space space = new Space();
    space.setId("42");
    space.setPrettyName("team");
    space.setMembers(new String[] { "john", "mary" });
    when(spaceService.getSpaceById("42")).thenReturn(space);
    when(identityManager.getOrCreateUserIdentity("john")).thenReturn(new Identity("3"));
    when(pollService.getPollOptionsNumber(anyLong(), any())).thenReturn(2);
    when(pollService.getPollTotalVotes(anyLong(), any())).thenReturn(1);
    ArgumentCaptor<StatisticData> stored = ArgumentCaptor.forClass(StatisticData.class);

    try (MockedStatic<ExoContainer> container = mockStatic(ExoContainer.class);
         MockedStatic<AnalyticsUtils> analytics = mockStatic(AnalyticsUtils.class)) {
      container.when(() -> ExoContainer.hasProfile("analytics")).thenReturn(true);
      analytics.when(() -> AnalyticsUtils.addSpaceStatistics(any(), any())).thenCallRealMethod();
      listener.onEvent(new Event<>(PollUtils.VOTE_POLL, "john", poll));
      analytics.verify(() -> AnalyticsUtils.addStatisticData(stored.capture()));
      analytics.verify(() -> AnalyticsUtils.addSpaceStatistics(any(StatisticData.class), same(space)));
    }

    StatisticData data = stored.getValue();
    assertEquals(PollUtils.VOTE_POLL_OPERATION_NAME, data.getOperation());
    assertEquals(42L, data.getSpaceId(), "the vote is stored with the space of its poll");
    assertEquals(3L, data.getUserId());
  }

}
