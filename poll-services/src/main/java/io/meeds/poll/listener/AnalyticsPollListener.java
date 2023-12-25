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
import static io.meeds.poll.utils.PollUtils.POLL_ACTIVITY_ID;
import static io.meeds.poll.utils.PollUtils.POLL_DURATION;
import static io.meeds.poll.utils.PollUtils.POLL_MODULE;
import static io.meeds.poll.utils.PollUtils.POLL_OPTIONS_NUMBER;
import static io.meeds.poll.utils.PollUtils.POLL_SPACE_MEMBERS_COUNT;
import static io.meeds.poll.utils.PollUtils.POLL_TOTAL_VOTES;
import static io.meeds.poll.utils.PollUtils.VOTE_POLL;
import static io.meeds.poll.utils.PollUtils.VOTE_POLL_OPERATION_NAME;
import static io.meeds.poll.utils.PollUtils.getPollDuration;

import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.analytics.model.StatisticData;
import org.exoplatform.analytics.utils.AnalyticsUtils;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.poll.model.Poll;
import io.meeds.poll.service.PollService;

@Asynchronous
@Component
public class AnalyticsPollListener extends Listener<String, Poll> {

  private static final String   POLL_ID         = "PollId";

  private static final String[] LISTENER_EVENTS = { "meeds.poll.createPoll", "meeds.poll.votePoll" };

  @Autowired
  private IdentityManager       identityManager;

  @Autowired
  private SpaceService          spaceService;

  @Autowired
  private PollService           pollService;

  @Autowired
  private ListenerService       listenerService;

  @PostConstruct
  public void init() {
    for (String eventName : LISTENER_EVENTS) {
      listenerService.addListener(eventName, this);
    }
  }

  @Override
  public void onEvent(Event<String, Poll> event) throws Exception {
    if (!ExoContainer.hasProfile("analytics")) {
      return;
    }
    Poll poll = event.getData();
    String operation = "";
    if (event.getEventName().equals(CREATE_POLL)) {
      operation = CREATE_POLL_OPERATION_NAME;
    } else if (event.getEventName().equals(VOTE_POLL)) {
      operation = VOTE_POLL_OPERATION_NAME;
    }
    String userName = event.getSource();
    long userId = 0;
    Identity identity = identityManager.getOrCreateUserIdentity(userName);
    if (identity != null) {
      userId = Long.parseLong(identity.getId());
    }
    StatisticData statisticData = new StatisticData();
    Space space = spaceService.getSpaceById(String.valueOf(poll.getSpaceId()));
    statisticData.setModule(POLL_MODULE);
    statisticData.setSubModule(POLL_MODULE);
    statisticData.setOperation(operation);
    statisticData.setUserId(userId);
    statisticData.addParameter(POLL_ID, poll.getId());
    statisticData.addParameter(POLL_ACTIVITY_ID, poll.getActivityId());
    statisticData.addParameter(POLL_OPTIONS_NUMBER,
                               pollService.getPollOptionsNumber(poll.getId(),
                                                                new org.exoplatform.services.security.Identity(userName)));
    statisticData.addParameter(POLL_DURATION, getPollDuration(poll));
    statisticData.addParameter(POLL_TOTAL_VOTES,
                               pollService.getPollTotalVotes(poll.getId(),
                                                             new org.exoplatform.services.security.Identity(userName)));
    statisticData.addParameter(POLL_SPACE_MEMBERS_COUNT, getSize(space.getMembers()));

    AnalyticsUtils.addStatisticData(statisticData);
  }

  private static int getSize(String[] array) {
    return array == null ? 0 : new HashSet<>(Arrays.asList(array)).size();
  }
}