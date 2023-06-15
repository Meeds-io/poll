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
package io.meeds.poll.listener;

import static io.meeds.poll.utils.PollUtils.CREATE_POLL;
import static io.meeds.poll.utils.PollUtils.CREATE_POLL_OPERATION_NAME;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_ACTIVITY_OBJECT_TYPE;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_GENERIC_EVENT_NAME;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_OBJECT_ID;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_OBJECT_TYPE;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_RECEIVER_ID;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_RECEIVER_TYPE;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_SENDER_ID;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_SENDER_TYPE;
import static io.meeds.poll.utils.PollUtils.GAMIFICATION_TRIGGER_NAME;
import static io.meeds.poll.utils.PollUtils.RECEIVE_POLL_VOTE_OPERATION_NAME;
import static io.meeds.poll.utils.PollUtils.VOTE_POLL;
import static io.meeds.poll.utils.PollUtils.VOTE_POLL_OPERATION_NAME;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.poll.model.Poll;

@Asynchronous
public class GamificationPollListener extends Listener<String, Poll> {

  private static final Log LOG = ExoLogger.getLogger(GamificationPollListener.class);

  private IdentityManager  identityManager;

  private ListenerService  listenerService;

  public GamificationPollListener(IdentityManager identityManager,
                                  ListenerService listenerService) {
    this.identityManager = identityManager;
    this.listenerService = listenerService;
  }

  @Override
  public void onEvent(Event<String, Poll> event) throws Exception {
    Poll poll = event.getData();
    String activityId = String.valueOf(poll.getActivityId());
    String creatorIdentityId = String.valueOf(poll.getCreatorId());
    if (event.getEventName().equals(CREATE_POLL)) {
      createRealizations(CREATE_POLL_OPERATION_NAME, activityId, creatorIdentityId, creatorIdentityId);
    } else if (event.getEventName().equals(VOTE_POLL)) {
      Identity identity = identityManager.getOrCreateUserIdentity(event.getSource());
      createRealizations(VOTE_POLL_OPERATION_NAME, activityId, identity.getId(), creatorIdentityId);
      createRealizations(RECEIVE_POLL_VOTE_OPERATION_NAME, activityId, creatorIdentityId, identity.getId());
    }
  }

  private void createRealizations(String gamificationEventName,
                                  String activityId,
                                  String earnerUsername,
                                  String receiverUsername) {
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(GAMIFICATION_TRIGGER_NAME, gamificationEventName);
      gam.put(GAMIFICATION_OBJECT_TYPE, GAMIFICATION_ACTIVITY_OBJECT_TYPE);
      gam.put(GAMIFICATION_OBJECT_ID, activityId);
      gam.put(GAMIFICATION_SENDER_TYPE, OrganizationIdentityProvider.NAME);
      gam.put(GAMIFICATION_SENDER_ID, earnerUsername);
      gam.put(GAMIFICATION_RECEIVER_TYPE, OrganizationIdentityProvider.NAME);
      gam.put(GAMIFICATION_RECEIVER_ID, receiverUsername);
      listenerService.broadcast(GAMIFICATION_GENERIC_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.warn("Error while broadcasting gamification event: {}", gam, e);
    }
  }

}
