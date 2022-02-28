/*
 * Copyright (C) 2022 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.poll.utils;

import java.text.ParseException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.rest.model.PollRestEntity;
import org.exoplatform.poll.rest.model.PollOptionRestEntity;

public class RestEntityBuilder {
  public static final DateTimeFormatter RFC_3339_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]")
                                                                              .withResolverStyle(ResolverStyle.LENIENT);

  public RestEntityBuilder() {
  }

  public static final Poll toPoll(PollRestEntity pollEntity) throws ParseException {
    ZonedDateTime createdDate = ZonedDateTime.parse(new Date().toString(), RFC_3339_FORMATTER.withZone(ZoneOffset.UTC));
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
