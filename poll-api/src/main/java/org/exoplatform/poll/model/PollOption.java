package org.exoplatform.poll.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollOption implements Cloneable {
  private static final long serialVersionUID = -5987422596945648223L;

  private long              id;

  private long              pollId;

  private String            pollOption;
}
