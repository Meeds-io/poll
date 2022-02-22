package org.exoplatform.poll.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "PollVote")
@ExoEntity
@Table(name = "POLL_VOTE")
public class PollVoteEntity implements Serializable {
  private static final long serialVersionUID = -6532033256222584847L;

  @Id
  @SequenceGenerator(name = "SEQ_POLL_VOTE_ID", sequenceName = "SEQ_POLL_VOTE_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_POLL_VOTE_ID")
  @Column(name = "POLL_VOTE_ID")
  private Long              id;

  @Column(name = "POLL_OPTION_ID", nullable = false)
  private Long              pollOptionId;

  @Column(name = "IDENTITY_ID", nullable = false)
  private Long              identityId;

}
