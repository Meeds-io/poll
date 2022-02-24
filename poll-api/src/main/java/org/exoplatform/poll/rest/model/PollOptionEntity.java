package org.exoplatform.poll.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollOptionEntity implements Serializable, Cloneable {
  private static final long serialVersionUID = -8155482590232413550L;

  private long              id;

  private long              pollId;

  private String            pollOption;

  @Override
  protected PollOptionEntity clone() { // NOSONAR
    return new PollOptionEntity(id, pollId, pollOption);
  }
}
