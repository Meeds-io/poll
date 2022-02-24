package org.exoplatform.poll.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollEntity implements Serializable, Cloneable {
  private static final long      serialVersionUID = -5019639622733003810L;

  private long                   id;

  private String                 question;

  private String                 startDate;

  private String                 endDate;

  private long                   creatorId;

  private long                   activityId;

  private List<PollOptionEntity> dateOptions;

  @Override
  public PollEntity clone() {// NOSONAR
    return new PollEntity(id, question, startDate, endDate, creatorId, activityId, dateOptions);
  }
}
