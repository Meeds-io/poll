package org.exoplatform.poll.service;

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.storage.PollStorage;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.List;

public class PollServiceImpl implements PollService {
  private static final Log LOG = ExoLogger.getLogger(PollServiceImpl.class);

  private PollStorage pollStorage;

  public PollServiceImpl(PollStorage pollStorage) {
    this.pollStorage = pollStorage;
  }

  @Override
  public Poll createPoll(Poll poll, List<PollOption> pollOptions, long userIdentityId) throws IllegalAccessException {
    if (userIdentityId <= 0) {
      throw new IllegalArgumentException("userIdentityId is mandatory");
    }
    if (poll == null) {
      throw new IllegalArgumentException("Poll is mandatory");
    }
    Poll createdPoll = pollStorage.createPoll(poll);
    List<PollOption> createdOptions = new ArrayList<>();
    pollOptions.forEach(pollOption -> {
      pollOption.setId(0);
      pollOption.setPollId(createdPoll.getId());
      pollStorage.createPollOption(pollOption);
      createdOptions.add(pollOption);
    });
    return createdPoll;
  }
}
