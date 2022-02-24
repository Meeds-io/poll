package org.exoplatform.poll.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Poll implements Cloneable {
  private long          id;

  private String        question;

  private ZonedDateTime startDate;

  private ZonedDateTime endDate;

  private long          creatorId;

  private long          activityId;

    public Poll clone() { // NOSONAR
        return new Poll(id,
                        question,
                        startDate,
                        endDate,
                        creatorId,
                        activityId);
    }
}
