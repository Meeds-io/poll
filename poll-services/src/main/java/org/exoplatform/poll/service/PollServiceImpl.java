package org.exoplatform.poll.service;

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.storage.PollStorage;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    List<PollOption> createdPollOptions = new ArrayList<>();
    pollOptions.forEach(pollOption -> {
      createdPollOptions.add(pollStorage.createPollOption(pollOption));
    });
    List<Long> polls = pollOptions.stream().map(PollOption::getId).collect(Collectors.toList());
    poll.setOptions(polls);
    Poll createdPoll = pollStorage.createPoll(poll);
    return createdPoll;
  }
}
