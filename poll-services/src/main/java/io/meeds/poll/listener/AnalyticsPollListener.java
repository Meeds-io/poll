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

import io.meeds.poll.model.Poll;
import io.meeds.poll.service.PollService;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.analytics.model.StatisticData;
import org.exoplatform.analytics.utils.AnalyticsUtils;

import java.util.concurrent.TimeUnit;

import static org.exoplatform.analytics.utils.AnalyticsUtils.addSpaceStatistics;

@Asynchronous
public class AnalyticsPollListener extends Listener<String, Poll> {

    public static final String CREATE_POLL_OPERATION_NAME = "createPoll";

    public static final String VOTE_POLL_OPERATION_NAME = "votePoll";

    private IdentityManager identityManager;

    private SpaceService spaceService;

    private PollService pollService;

    @Override
    public void onEvent(Event<String, Poll> event) throws Exception {
        Poll poll = event.getData();
        String operation = "";
        switch (event.getEventName()) {
            case "meeds.poll.createPoll":
                operation = CREATE_POLL_OPERATION_NAME;
                break;
            case "meeds.poll.votePoll":
                operation = VOTE_POLL_OPERATION_NAME;
                break;
        }
        long userId = 0;
        Identity identity = getIdentityManager().getOrCreateIdentity(OrganizationIdentityProvider.NAME, event.getSource());
        if (identity != null) {
            userId = Long.parseLong(identity.getId());
        }
        StatisticData statisticData = new StatisticData();
        Space space = getSpaceService().getSpaceById(String.valueOf(poll.getSpaceId()));

        statisticData.setModule("poll");
        statisticData.setSubModule("poll");
        statisticData.setOperation(operation);
        statisticData.setUserId(userId);
        statisticData.addParameter("PollId", poll.getId());
        statisticData.addParameter("ActivityId", poll.getActivityId());
        statisticData.addParameter("NumberOptions", getPollService().getNumberOptions(poll.getId()));
        statisticData.addParameter("ChosenDuration", getDuration(poll));
        statisticData.addParameter("NumberVotes", getPollService().getNumberVotes(poll.getId()));
        statisticData.addParameter("NumberSpaceMembers", space.getMembers().length);
        if (space != null) {
            addSpaceStatistics(statisticData, space);
        }
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

    private long getDuration(Poll poll) {
        long duration = Math.abs(poll.getEndDate().getTime() - poll.getCreatedDate().getTime());
        return TimeUnit.DAYS.convert(duration, TimeUnit.MILLISECONDS);
    }
}
