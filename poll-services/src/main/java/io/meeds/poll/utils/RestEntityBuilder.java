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
package io.meeds.poll.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.rest.model.PollOptionRestEntity;
import io.meeds.poll.rest.model.PollRestEntity;

public class RestEntityBuilder {
  
  private static final String ONE_DAY_DURATION = "1day";
  
  private static final String THREE_DAYS_DURATION = "3days";
  
  private static final String ONE_WEEK_DURATION = "1week";
  
  private static final String TWO_WEEKS_DURATION = "2weeks";

  private RestEntityBuilder() {
  }
  
  public static final PollRestEntity fromPoll(Poll poll,
                                              List<PollOptionRestEntity> pollOptionRestEntities) {
    PollRestEntity pollRestEntity = new PollRestEntity();
    pollRestEntity.setId(poll.getId());
    pollRestEntity.setQuestion(poll.getQuestion());
    pollRestEntity.setOptions(pollOptionRestEntities);
    pollRestEntity.setEndDateTime(poll.getEndDate().getTime());
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    if (identityManager.getIdentity(String.valueOf(poll.getCreatorId())) != null) {
      pollRestEntity.setCreator(identityManager.getIdentity(String.valueOf(poll.getCreatorId())).getRemoteId());
    }
    return pollRestEntity;
  }

  public static final PollOptionRestEntity fromPollOption(PollOption pollOption, int votes, boolean voted) {
    PollOptionRestEntity pollOptionRestEntity = new PollOptionRestEntity();
    pollOptionRestEntity.setId(pollOption.getId());
    pollOptionRestEntity.setDescription(pollOption.getDescription());
    pollOptionRestEntity.setVotes(votes);
    pollOptionRestEntity.setVoted(voted);
    return pollOptionRestEntity;
  }

  public static final Poll toPoll(PollRestEntity pollRestEntity) {
    Date createdDate = new Date();
    ZonedDateTime createdZonedDateTime = ZonedDateTime.ofInstant(createdDate.toInstant(), ZoneOffset.UTC);
    ZonedDateTime endZonedDateTime = null;
    switch (pollRestEntity.getDuration()) {
    case ONE_DAY_DURATION:
      endZonedDateTime = createdZonedDateTime.plusDays(1);
      break;
    case THREE_DAYS_DURATION:
      endZonedDateTime = createdZonedDateTime.plusDays(3);
      break;
    case ONE_WEEK_DURATION:
      endZonedDateTime = createdZonedDateTime.plusDays(7);
      break;
    case TWO_WEEKS_DURATION:
      endZonedDateTime = createdZonedDateTime.plusDays(14);
      break;
    default:
      throw new IllegalStateException("Unexpected value: " + pollRestEntity.getDuration());
    }
    Poll poll = new Poll();
    poll.setQuestion(pollRestEntity.getQuestion());
    poll.setCreatedDate(createdDate);
    poll.setEndDate(PollUtils.toDate(endZonedDateTime));
    return poll;
  }

  public static final List<PollOption> toPollOptions(List<PollOptionRestEntity> pollOptionEntities) {
    return pollOptionEntities.stream().map(pollOptionEntity -> {
      PollOption pollOption = new PollOption();
      pollOption.setDescription(pollOptionEntity.getDescription());
      return pollOption;
    }).collect(Collectors.toList());
  }
}
