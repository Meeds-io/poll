package org.exoplatform.poll.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "PollOption")
@ExoEntity
@Table(name = "POLL_OPTION")
public class PollOptionEntity implements Serializable {

  private static final long serialVersionUID = 8803249235458041880L;

  @Id
  @SequenceGenerator(name = "SEQ_POLL_OPTION_ID", sequenceName = "SEQ_POLL_OPTION_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_POLL_OPTION_ID")
  @Column(name = "POLL_OPTION_ID")
  private Long              id;

  @Column(name = "POLL_ID", nullable = false)
  private Long              pollId;

  @Column(name = "POLL_OPTION_CONTENT", nullable = false)
  private String              pollOption;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getPollId() {
    return pollId;
  }

  public void setPollId(Long pollId) {
    this.pollId = pollId;
  }

  public String getPollOption() {
    return pollOption;
  }

  public void setPollOption(String pollOption) {
    this.pollOption = pollOption;
  }
}
