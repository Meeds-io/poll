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

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.rest.model.PollEntity;
import org.exoplatform.poll.rest.model.PollOptionEntity;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.stream.Collectors;

public class RestEntityBuilder {
  public static final DateTimeFormatter RFC_3339_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]")
                                                                              .withResolverStyle(ResolverStyle.LENIENT);

  public RestEntityBuilder() {
  }

  public static final Poll toPoll(PollEntity pollEntity,String timeZoneId) throws ParseException {
    ZoneId userTimeZone = timeZoneId == null ? ZoneOffset.UTC : ZoneId.of(timeZoneId);
    ZonedDateTime startDateTime = ZonedDateTime.parse(pollEntity.getStartDate(), RFC_3339_FORMATTER.withZone(userTimeZone));
    ZonedDateTime endDateTime = null;
    switch (pollEntity.getPollDuration()) {
    case "1day":
      endDateTime = startDateTime.plusDays(1);
      break;
    case "3day":
      endDateTime = startDateTime.plusDays(3);
      break;
    case "1week":
      endDateTime = startDateTime.plusDays(7);
      break;
    case "2week":
      endDateTime = startDateTime.plusDays(14);
      break;
      default:
        throw new IllegalStateException("Unexpected value: " + pollEntity.getPollDuration());
    }
    return new Poll(0, pollEntity.getQuestion(), startDateTime, endDateTime, pollEntity.getCreatorId(), pollEntity.getActivityId());
  }

  public static final List<PollOption> toPollOption(List<PollOptionEntity> pollOptionEntities) {
    return pollOptionEntities.stream().map(pollOption -> {
      PollOption pollOption1 = new PollOption();
      pollOption1.setId(0);
      pollOption1.setPollId(0);
      pollOption1.setDescription(pollOption.getPollOption());
      return pollOption1;
    }).collect(Collectors.toList());
  }
}
