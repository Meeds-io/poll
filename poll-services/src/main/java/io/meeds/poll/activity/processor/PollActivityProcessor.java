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
package io.meeds.poll.activity.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.BaseActivityProcessorPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;

import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.rest.model.PollOptionRestEntity;
import io.meeds.poll.rest.model.PollRestEntity;
import io.meeds.poll.service.PollService;
import io.meeds.poll.utils.PollUtils;
import io.meeds.poll.utils.RestEntityBuilder;

public class PollActivityProcessor extends BaseActivityProcessorPlugin {

  private PollService      pollService;

  private static final Log LOG = ExoLogger.getLogger(PollActivityProcessor.class);

  public PollActivityProcessor(PollService pollService, InitParams initParams) {
    super(initParams);
    this.pollService = pollService;
  }

  @Override
  public void processActivity(ExoSocialActivity activity) {
    if (activity.isComment() || activity.getType() == null || !activity.getTemplateParams().containsKey(PollUtils.POLL_ID)) {
      return;
    }
    if (activity.getLinkedProcessedEntities() == null) {
      activity.setLinkedProcessedEntities(new HashMap<>());
    }
    PollRestEntity pollRestEntity = (PollRestEntity) activity.getLinkedProcessedEntities().get(PollUtils.POLL_ACTIVITY_TYPE);

    if (pollRestEntity == null) {
      Identity currentIdentity = ConversationState.getCurrent().getIdentity();
      String pollId = activity.getTemplateParams().get(PollUtils.POLL_ID);
      try {
        Poll poll = pollService.getPollById(Long.parseLong(pollId), currentIdentity);
        List<PollOption> pollOptions = pollService.getPollOptionsByPollId(Long.parseLong(pollId), currentIdentity);
        List<PollOptionRestEntity> pollOptionRestEntities = new ArrayList<>();
        for (PollOption pollOption : pollOptions) {
          int pollOptionVotes = pollService.getPollOptionTotalVotes(pollOption.getId(),
                                                                    String.valueOf(poll.getSpaceId()),
                                                                    currentIdentity);
          boolean isPollOptionVoted = pollService.isPollOptionVoted(pollOption.getId(),
                                                                    String.valueOf(poll.getSpaceId()),
                                                                    currentIdentity);
          PollOptionRestEntity pollOptionRestEntity = RestEntityBuilder.fromPollOption(pollOption,
                                                                                       pollOptionVotes,
                                                                                       isPollOptionVoted);
          pollOptionRestEntities.add(pollOptionRestEntity);
        }
        pollRestEntity = RestEntityBuilder.fromPoll(poll, pollOptionRestEntities);
      } catch (IllegalAccessException e) {
        LOG.warn("User {} attempt to access a non authorized poll with id {}", currentIdentity.getUserId(), pollId, e);
      }
      activity.getLinkedProcessedEntities().put(PollUtils.POLL_ACTIVITY_TYPE, pollRestEntity);
    }
  }
}
