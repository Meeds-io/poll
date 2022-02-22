package org.exoplatform.poll.storage;

import org.exoplatform.poll.dao.PollDAO;
import org.exoplatform.poll.entity.PollEntity;
import org.exoplatform.poll.model.Poll;

public class PollStorage {
  private PollDAO pollDAO;

  public PollStorage(PollDAO pollDAO) {
    this.pollDAO = pollDAO;
  }

  public Poll createPoll(Poll poll) {
      PollEntity pollEntity = new PollEntity();
      pollEntity.setId(null);
      pollEntity.setPollQuestion(poll.getQuestion());
      return null;
  }
}
