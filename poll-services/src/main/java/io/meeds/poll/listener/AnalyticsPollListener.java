package io.meeds.poll.listener;

import io.meeds.poll.model.Poll;
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

import static org.exoplatform.analytics.utils.AnalyticsUtils.addSpaceStatistics;

@Asynchronous
public class AnalyticsPollListener extends Listener<String, Poll> {

    public static final String CREATE_POLL_OPERATION_NAME = "createPoll";

    public static final String VOTE_POLL_OPERATION_NAME = "votePoll";

    private IdentityManager identityManager;

    private SpaceService spaceService;

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

        statisticData.setModule("contents");
        statisticData.setSubModule("contents");
        statisticData.setOperation(operation);
        statisticData.setUserId(userId);
        statisticData.addParameter("PollId", poll.getId());
        statisticData.addParameter("ActivityId", poll.getActivityId());
        statisticData.addParameter("NumberOptions", poll.getId());
        statisticData.addParameter("ChosenDuration", poll.getId());
        statisticData.addParameter("NumberVotes", poll.getId());
        statisticData.addParameter("NumberSpaceMembers", poll.getId());
        Space space = getSpaceService().getSpaceById(String.valueOf(poll.getSpaceId()));
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
}
