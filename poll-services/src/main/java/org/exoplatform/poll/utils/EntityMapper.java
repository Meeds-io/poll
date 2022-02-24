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

  public static Poll fromEntity(PollEntity pollEntity) {
    if (pollEntity == null) {
      return null;
    }
    return new Poll(pollEntity.getId(),
                    pollEntity.getPollQuestion(),
                    RestUtils.fromDate(pollEntity.getStart_date()),
                    RestUtils.fromDate(pollEntity.getEnd_date()),
                    pollEntity.getIdentityId(),
                    pollEntity.getActivity_id());
  }

  public static PollEntity toEntity(Poll poll) {
    if (poll == null) {
      return null;
    }
    PollEntity pollEntity = new PollEntity();
    pollEntity.setId(poll.getId());
    pollEntity.setPollQuestion(poll.getQuestion());
    pollEntity.setStart_date(RestUtils.toDate(poll.getStartDate()));
    pollEntity.setEnd_date(RestUtils.toDate(poll.getEndDate()));
    pollEntity.setActivity_id(poll.getActivityId());
    pollEntity.setIdentityId(poll.getCreatorId());
    return pollEntity;
  }

  public static PollOption optionFromEntity(PollOptionEntity pollOptionEntity) {
    if (pollOptionEntity == null) {
      return null;
    }
    return new PollOption(pollOptionEntity.getId(), pollOptionEntity.getPollId(), pollOptionEntity.getPollOption());
  }

  public static PollOptionEntity optionToEntity(PollOption pollOption) {
    if (pollOption == null) {
      return null;
    }
    PollOptionEntity pollOptionEntity = new PollOptionEntity();
    pollOptionEntity.setId(pollOption.getId());
    pollOptionEntity.setPollId(pollOption.getPollId());
    return pollOptionEntity;
  }

}
