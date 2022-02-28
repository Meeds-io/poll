package org.exoplatform.poll.utils;

import org.exoplatform.poll.entity.PollEntity;
import org.exoplatform.poll.entity.PollOptionEntity;
import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class EntityMapper {
  private static final Log LOG = ExoLogger.getLogger(EntityMapper.class);

  public EntityMapper() {
  }

  public static Poll fromPollEntity(PollEntity pollEntity) {
    if (pollEntity == null) {
      return null;
    }
    return new Poll(pollEntity.getId(),
                    pollEntity.getQuestion(),
                    RestUtils.fromDate(pollEntity.getCreatedDate()),
                    RestUtils.fromDate(pollEntity.getEndDate()),
                    pollEntity.getCreatorId(),
                    pollEntity.getActivityId());
  }

  public static PollEntity toPollEntity(Poll poll) {
    if (poll == null) {
      return null;
    }
    PollEntity pollEntity = new PollEntity();
    pollEntity.setId(poll.getId());
    pollEntity.setQuestion(poll.getQuestion());
    pollEntity.setCreatedDate(RestUtils.toDate(poll.getCreatedDate()));
    pollEntity.setEndDate(RestUtils.toDate(poll.getEndDate()));
    pollEntity.setActivityId(poll.getActivityId());
    pollEntity.setCreatorId(poll.getCreatorId());
    return pollEntity;
  }

  public static PollOption fromPollOptionEntity(PollOptionEntity pollOptionEntity) {
    if (pollOptionEntity == null) {
      return null;
    }
    return new PollOption(pollOptionEntity.getId(), pollOptionEntity.getPollId(), pollOptionEntity.getDescription());
  }

  public static PollOptionEntity toPollOptionEntity(PollOption pollOption, Long pollEntityId) {
    if (pollOption == null) {
      return null;
    }
    PollOptionEntity pollOptionEntity = new PollOptionEntity();
    pollOptionEntity.setId(pollOption.getId());
    pollOptionEntity.setPollId(pollEntityId);
    return pollOptionEntity;
  }

}
