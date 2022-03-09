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
package org.exoplatform.poll.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.rest.model.PollRestEntity;
import org.exoplatform.poll.rest.model.PollOptionRestEntity;

public class RestEntityBuilder {
  
  private static final String ONE_DAY_DURATION = "1day";
  
  private static final String THREE_DAYS_DURATION = "3days";
  
  private static final String ONE_WEEK_DURATION = "1week";
  
  private static final String TWO_WEEKS_DURATION = "2weeks";

  private RestEntityBuilder() {
  }
  
  public static final PollRestEntity fromPoll(Poll poll, List<PollOption> pollOptions) {
    PollRestEntity pollRestEntity = new PollRestEntity();
    pollRestEntity.setQuestion(poll.getQuestion());
    List<PollOptionRestEntity> pollOptionRestEntities = new ArrayList<>();
    for (PollOption pollOption : pollOptions) {
      PollOptionRestEntity pollOptionRestEntity = new PollOptionRestEntity();
      pollOptionRestEntity.setDescription(pollOption.getDescription());
      //TODO setVotes
      pollOptionRestEntity.setVotes("0");
      pollOptionRestEntities.add(pollOptionRestEntity);
    }
    pollRestEntity.setOptions(pollOptionRestEntities);
    pollRestEntity.setEndDateTime(poll.getEndDate().getTime());
    return pollRestEntity;
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
