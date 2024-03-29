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

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.BaseActivityProcessorPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.manager.ActivityManager;

import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.rest.model.PollOptionRestEntity;
import io.meeds.poll.rest.model.PollRestEntity;
import io.meeds.poll.service.PollService;
import io.meeds.poll.utils.PollUtils;
import io.meeds.poll.utils.RestEntityBuilder;

@Component
public class PollActivityProcessor extends BaseActivityProcessorPlugin {

  private static final Log    LOG                     = ExoLogger.getLogger(PollActivityProcessor.class);

  private static final String ACTIVITY_PROCESSOR_NAME = "PollActivityProcessor";

  @Value("${meeds.poll.activity.processor.priority:30}")
  private static int          processorPriority;

  @Autowired
  private PollService         pollService;

  @Autowired
  private ActivityManager     activityManager;

  public PollActivityProcessor() {
    super(getPriorityInitParam());
  }

  @PostConstruct
  public void init() {
    activityManager.addProcessor(this);
  }

  @Override
  public String getName() {
    return ACTIVITY_PROCESSOR_NAME;
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
      ConversationState currentState = ConversationState.getCurrent();
      if (currentState == null) {
        LOG.warn("Error processing Poll due to null ConversationState.getCurrent() inside activity storage layer processor. Ignore processing.");
        return;
      }
      Identity currentIdentity = currentState.getIdentity();
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

  private static InitParams getPriorityInitParam() {
    return new InitParams() {
      private static final long serialVersionUID = 6692556831691417605L;

      @Override
      public ValueParam getValueParam(String name) {
        if (StringUtils.equals("priority", name)) {
          return new ValueParam() {
            @Override
            public String getValue() {
              return String.valueOf(processorPriority);
            }
          };
        } else {
          return super.getValueParam(name);
        }
      }
    };
  }

}