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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.rest.model.PollRestEntity;
import org.exoplatform.poll.rest.model.PollOptionRestEntity;

public class RestEntityBuilder {

  private RestEntityBuilder() {
  }

  public static final Poll toPoll(PollRestEntity pollEntity) {
    ZonedDateTime createdDate = ZonedDateTime.ofInstant(new Date().toInstant(),ZoneOffset.UTC);
    ZonedDateTime endDate = null;
    switch (pollEntity.getDuration()) {
    case "1day":
      endDate = createdDate.plusDays(1);
      break;
    case "3day":
      endDate = createdDate.plusDays(3);
      break;
    case "1week":
      endDate = createdDate.plusDays(7);
      break;
    case "2week":
      endDate = createdDate.plusDays(14);
      break;
    default:
      throw new IllegalStateException("Unexpected value: " + pollEntity.getDuration());
    }
    Poll poll = new Poll();
    poll.setQuestion(pollEntity.getQuestion());
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
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
