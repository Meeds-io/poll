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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

import static io.meeds.poll.utils.PollUtils.*;

@Asynchronous
@Component
public class GamificationPollListener extends Listener<String, Poll> {

  private static final Log      LOG             = ExoLogger.getLogger(GamificationPollListener.class);

  private static final String[] LISTENER_EVENTS = { "meeds.poll.createPoll", "meeds.poll.votePoll" };

  @Autowired
  private IdentityManager       identityManager;

  @Autowired
  private ListenerService       listenerService;

  @PostConstruct
  public void init() {
    for (String eventName : LISTENER_EVENTS) {
      listenerService.addListener(eventName, this);
    }
  }

  @Override
  public void onEvent(Event<String, Poll> event) {
    Poll poll = event.getData();
    String activityId = String.valueOf(poll.getActivityId());
    String creatorIdentityId = String.valueOf(poll.getCreatorId());
    String eventDetails;
    if (event.getEventName().equals(CREATE_POLL)) {
      eventDetails = "{spaceId: " + poll.getSpaceId() + "}";
      createRealizations(CREATE_POLL_OPERATION_NAME, activityId, creatorIdentityId, creatorIdentityId, eventDetails);
    } else if (event.getEventName().equals(VOTE_POLL)) {
      eventDetails = "{spaceId: " + poll.getSpaceId() + ", activityId: " + activityId + "}";
      Identity identity = identityManager.getOrCreateUserIdentity(event.getSource());
      createRealizations(VOTE_POLL_OPERATION_NAME, activityId, identity.getId(), creatorIdentityId, eventDetails);
      createRealizations(RECEIVE_POLL_VOTE_OPERATION_NAME, activityId, creatorIdentityId, identity.getId(), eventDetails);
    }
  }

  private void createRealizations(String gamificationEventName,
                                  String activityId,
                                  String earnerUsername,
                                  String receiverUsername,
                                  String eventDetails) {
    Map<String, String> gam = new HashMap<>();
    try {
      gam.put(GAMIFICATION_TRIGGER_NAME, gamificationEventName);
      gam.put(GAMIFICATION_OBJECT_TYPE, GAMIFICATION_ACTIVITY_OBJECT_TYPE);
      gam.put(GAMIFICATION_OBJECT_ID, activityId);
      gam.put(GAMIFICATION_SENDER_TYPE, OrganizationIdentityProvider.NAME);
      gam.put(GAMIFICATION_SENDER_ID, earnerUsername);
      gam.put(GAMIFICATION_RECEIVER_TYPE, OrganizationIdentityProvider.NAME);
      gam.put(GAMIFICATION_RECEIVER_ID, receiverUsername);
      gam.put(GAMIFICATION_EVENT_DETAILS, eventDetails);
      listenerService.broadcast(GAMIFICATION_GENERIC_EVENT_NAME, gam, null);
    } catch (Exception e) {
      LOG.warn("Error while broadcasting gamification event: {}", gam, e);
    }
  }

}
