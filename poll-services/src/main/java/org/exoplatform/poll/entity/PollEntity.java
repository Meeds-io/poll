package org.exoplatform.poll.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "Poll")
@ExoEntity
@Table(name = "POLL")
public class PollEntity implements Serializable {

  private static final long serialVersionUID = 5290107403575974438L;

  @Id
  @SequenceGenerator(name = "SEQ_POLL_ID", sequenceName = "SEQ_POLL_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_POLL_ID")
  @Column(name = "POLL_ID", nullable = false)
  private Long              id;

  @Column(name = "POLL_QUESTION")
  private String            pollQuestion;

  @Column(name = "START_DATE", nullable = false)
  private Date              start_date;

  @Column(name = "END_DATE", nullable = false)
  private Date              end_date;

  @Column(name = "ACTIVITY_ID")
  private Long              activity_id;

  @Column(name = "IDENTITY_ID", nullable = false)
  private Long              identityId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPollQuestion() {
    return pollQuestion;
  }

  public void setPollQuestion(String pollQuestion) {
    this.pollQuestion = pollQuestion;
  }

  public Date getStart_date() {
    return start_date;
  }

  public void setStart_date(Date start_date) {
    this.start_date = start_date;
  }

  public Date getEnd_date() {
    return end_date;
  }

  public void setEnd_date(Date end_date) {
    this.end_date = end_date;
  }

  public Long getActivity_id() {
    return activity_id;
  }

  public void setActivity_id(Long activity_id) {
    this.activity_id = activity_id;
  }

  public Long getIdentityId() {
    return identityId;
  }

  public void setIdentityId(Long identityId) {
    this.identityId = identityId;
  }
}
