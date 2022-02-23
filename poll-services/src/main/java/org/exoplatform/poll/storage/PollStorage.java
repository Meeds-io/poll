package org.exoplatform.poll.storage;

import org.exoplatform.poll.dao.PollDAO;
import org.exoplatform.poll.dao.PollOptionDAO;
import org.exoplatform.poll.entity.PollEntity;
import org.exoplatform.poll.entity.PollOptionEntity;
import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.utils.EntityMapper;

public class PollStorage {
  private PollDAO       pollDAO;

  private PollOptionDAO pollOptionDAO;

  public PollStorage(PollDAO pollDAO, PollOptionDAO pollOptionDAO) {
    this.pollDAO = pollDAO;
    this.pollOptionDAO = pollOptionDAO;
  }

  public Poll createPoll(Poll poll) {
    PollEntity pollEntity = EntityMapper.toEntity(poll);
    pollEntity.setId(null);
    pollEntity = pollDAO.create(pollEntity);
    return EntityMapper.fromEntity(pollEntity);
  }

  public PollOption createPollOption(PollOption pollOption) {
    PollOptionEntity pollOptionEntity = EntityMapper.optionToEntity(pollOption);
    pollOptionEntity.setId(null);
    pollOptionEntity.setPollOption(pollOption.getPollOption());
    pollOptionEntity = pollOptionDAO.create(pollOptionEntity);
    return EntityMapper.optionFromEntity(pollOptionEntity);
  }
}
