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

import java.util.Arrays;
import java.util.HashSet;

import org.exoplatform.analytics.model.StatisticData;
import org.exoplatform.analytics.utils.AnalyticsUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.poll.model.Poll;
import io.meeds.poll.service.PollService;
import io.meeds.poll.utils.PollUtils;

@Asynchronous
public class AnalyticsPollListener extends Listener<String, Poll> {

  private static final String CREATE_POLL_OPERATION_NAME = "createPoll";

  private static final String VOTE_POLL_OPERATION_NAME   = "votePoll";

  private static final String POLL_MODULE                = "Poll";

  private static final String POLL_ID                    = "PollId";

  private static final String POLL_ACTIVITY_ID           = "PollActivityId";

  private static final String POLL_OPTIONS_NUMBER        = "PollOptionsNumber";

  private static final String POLL_DURATION              = "PollDuration";

  private static final String POLL_TOTAL_VOTES           = "PollTotalVotes";

  private static final String POLL_SPACE_MEMBERS_COUNT   = "PollSpaceMembersCount";

  private IdentityManager     identityManager;

  private SpaceService        spaceService;

  private PollService         pollService;

  @Override
  public void onEvent(Event<String, Poll> event) throws Exception {
    Poll poll = event.getData();
    String operation = "";
    if (event.getEventName().equals(PollUtils.CREATE_POLL)) {
      operation = CREATE_POLL_OPERATION_NAME;
    } else if (event.getEventName().equals(PollUtils.VOTE_POLL)) {
      operation = VOTE_POLL_OPERATION_NAME;
    }
    String userName = event.getSource();
    long userId = 0;
    Identity identity = getIdentityManager().getOrCreateIdentity(OrganizationIdentityProvider.NAME, userName);
    if (identity != null) {
      userId = Long.parseLong(identity.getId());
    }
    StatisticData statisticData = new StatisticData();
    Space space = getSpaceService().getSpaceById(String.valueOf(poll.getSpaceId()));
    statisticData.setModule(POLL_MODULE);
    statisticData.setSubModule(POLL_MODULE);
    statisticData.setOperation(operation);
    statisticData.setUserId(userId);
    statisticData.addParameter(POLL_ID, poll.getId());
    statisticData.addParameter(POLL_ACTIVITY_ID, poll.getActivityId());
    statisticData.addParameter(POLL_OPTIONS_NUMBER, getPollService().getPollOptionsNumber(poll.getId(), new org.exoplatform.services.security.Identity(userName)));
    statisticData.addParameter(POLL_DURATION, PollUtils.getPollDuration(poll));
    statisticData.addParameter(POLL_TOTAL_VOTES, getPollService().getPollTotalVotes(poll.getId(), new org.exoplatform.services.security.Identity(userName)));
    statisticData.addParameter(POLL_SPACE_MEMBERS_COUNT, getSize(space.getMembers()));

    AnalyticsUtils.addStatisticData(statisticData);
  }

  public IdentityManager getIdentityManager() {
    if (identityManager == null) {
      identityManager = ExoContainerContext.getService(IdentityManager.class);
    }
    return identityManager;
  }

  public SpaceService getSpaceService() {
    if (spaceService == null) {
      spaceService = ExoContainerContext.getService(SpaceService.class);
    }
    return spaceService;
  }

  public PollService getPollService() {
    if (pollService == null) {
      pollService = ExoContainerContext.getService(PollService.class);
    }
    return pollService;
  }
  
  private static int getSize(String[] array) {
    return array == null ? 0 : new HashSet<>(Arrays.asList(array)).size();
  }
}
