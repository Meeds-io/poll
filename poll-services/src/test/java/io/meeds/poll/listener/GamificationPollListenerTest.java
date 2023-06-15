/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.poll.listener;

import static io.meeds.poll.utils.PollUtils.CREATE_POLL;
import static io.meeds.poll.utils.PollUtils.CREATE_POLL_OPERATION_NAME;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_ACTIVITY_OBJECT_TYPE;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_GENERIC_EVENT_NAME;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_OBJECT_ID;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_OBJECT_TYPE;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_RECEIVER_ID;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_SENDER_ID;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_TRIGGER_NAME;
import static io.meeds.poll.utils.PollUtils.RECEIVE_POLL_VOTE_OPERATION_NAME;
import static io.meeds.poll.utils.PollUtils.VOTE_POLL;
import static io.meeds.poll.utils.PollUtils.VOTE_POLL_OPERATION_NAME;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.poll.model.Poll;

@RunWith(MockitoJUnitRunner.class)
public class GamificationPollListenerTest {

  private static final long   CREATOR_ID  = 555l;

  private static final String MODIFIER_ID = "556";

  private static final long   ACTIVITY_ID = 2553l;

  private static final String MODIFIER    = "modfier";

  @Mock
  private ListenerService     listenerService;

  @Mock
  private IdentityManager     identityManager;

  @Mock
  private Poll                poll;

  @Mock
  private Identity            modifierIdentity;

  @Mock
  private Event<String, Poll> event;

  @Test
  public void testOnCreateEvent() throws Exception {
    GamificationPollListener gamificationListener = new GamificationPollListener(identityManager, listenerService);
    when(event.getEventName()).thenReturn(CREATE_POLL);
    when(event.getData()).thenReturn(poll);
    when(poll.getCreatorId()).thenReturn(CREATOR_ID);
    when(poll.getActivityId()).thenReturn(ACTIVITY_ID);

    gamificationListener.onEvent(event);

    verify(listenerService,
           times(1)).broadcast(eq(GAMIFICATION_GENERIC_EVENT_NAME),
                               argThat((Map<String, String> source) -> source.get(GAMIFICATION_TRIGGER_NAME)
                                                                             .equals(CREATE_POLL_OPERATION_NAME)
                                   && source.get(GAMIFICATION_SENDER_ID)
                                            .equals(String.valueOf(CREATOR_ID))
                                   && source.get(GAMIFICATION_RECEIVER_ID)
                                            .equals(String.valueOf(CREATOR_ID))
                                   && source.get(GAMIFICATION_OBJECT_TYPE)
                                            .equals(GAMIFICATION_ACTIVITY_OBJECT_TYPE)
                                   && source.get(GAMIFICATION_OBJECT_ID)
                                            .equals(String.valueOf(ACTIVITY_ID))),
                               eq(null));

  }

  @Test
  public void testOnVoteEvent() throws Exception {
    GamificationPollListener gamificationListener = new GamificationPollListener(identityManager, listenerService);
    when(event.getEventName()).thenReturn(VOTE_POLL);
    when(event.getSource()).thenReturn(MODIFIER);
    when(event.getData()).thenReturn(poll);
    when(poll.getCreatorId()).thenReturn(CREATOR_ID);
    when(poll.getActivityId()).thenReturn(ACTIVITY_ID);
    when(identityManager.getOrCreateUserIdentity(MODIFIER)).thenReturn(modifierIdentity);
    when(modifierIdentity.getId()).thenReturn(MODIFIER_ID);

    gamificationListener.onEvent(event);

    verify(listenerService,
           times(1)).broadcast(eq(GAMIFICATION_GENERIC_EVENT_NAME),
                               argThat((Map<String, String> source) -> source.get(GAMIFICATION_TRIGGER_NAME)
                                                                             .equals(VOTE_POLL_OPERATION_NAME)
                                   && source.get(GAMIFICATION_SENDER_ID)
                                            .equals(MODIFIER_ID)
                                   && source.get(GAMIFICATION_RECEIVER_ID)
                                            .equals(String.valueOf(CREATOR_ID))
                                   && source.get(GAMIFICATION_OBJECT_TYPE)
                                            .equals(GAMIFICATION_ACTIVITY_OBJECT_TYPE)
                                   && source.get(GAMIFICATION_OBJECT_ID)
                                            .equals(String.valueOf(ACTIVITY_ID))),
                               eq(null));

    verify(listenerService,
           times(1)).broadcast(eq(GAMIFICATION_GENERIC_EVENT_NAME),
                               argThat((Map<String, String> source) -> source.get(GAMIFICATION_TRIGGER_NAME)
                                                                             .equals(RECEIVE_POLL_VOTE_OPERATION_NAME)
                                   && source.get(GAMIFICATION_SENDER_ID)
                                            .equals(String.valueOf(CREATOR_ID))
                                   && source.get(GAMIFICATION_RECEIVER_ID)
                                            .equals(MODIFIER_ID)
                                   && source.get(GAMIFICATION_OBJECT_TYPE)
                                            .equals(GAMIFICATION_ACTIVITY_OBJECT_TYPE)
                                   && source.get(GAMIFICATION_OBJECT_ID)
                                            .equals(String.valueOf(ACTIVITY_ID))),
                               eq(null));

  }

}
