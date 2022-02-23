package org.exoplatform.poll.service;

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;

import java.util.List;

public interface PollService {
  /**
   * Creates a new poll
   *
   * @param poll {@link Poll} object to create
   * @param username User name creating poll
   * @return created {@link Poll} with generated technical identifier
   * @throws IllegalAccessException when user is not authorized to create a poll
   *           for the designated owner defined in object
   */
  Poll createPoll(Poll poll, List<PollOption> pollOptions, long username) throws IllegalAccessException;
}
