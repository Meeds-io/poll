package org.exoplatform.poll.utils;

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.rest.model.PollEntity;
import org.exoplatform.poll.rest.model.PollOptionEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RestEntityBuilder {
  public RestEntityBuilder() {
  }

  public static final Poll toPoll(PollEntity pollEntity,String timeZoneId) throws ParseException {
    ZoneId userTimeZone = timeZoneId == null ? ZoneOffset.UTC : ZoneId.of(timeZoneId);
    ZonedDateTime startDateTime = ZonedDateTime.parse(pollEntity.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss").withZone(userTimeZone));
    ZonedDateTime endDateTime = ZonedDateTime.parse(pollEntity.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss").withZone(userTimeZone));
    return new Poll(0, pollEntity.getQuestion(), startDateTime, endDateTime, pollEntity.getCreatorId(), pollEntity.getActivityId());
  }

  public static final List<PollOption> toPollOption(List<PollOptionEntity> pollOptionEntities) {
    return pollOptionEntities.stream().map(pollOption -> {
      PollOption pollOption1 = new PollOption();
      pollOption1.setId(0);
      pollOption1.setPollId(0);
      pollOption1.setPollOption(pollOption.getPollOption());
      return pollOption1;
    }).collect(Collectors.toList());
  }
}
