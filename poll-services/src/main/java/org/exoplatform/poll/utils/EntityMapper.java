/*
 * Copyright (C) 2022 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
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
                    RestUtils.fromDate(pollEntity.getStartDate()),
                    RestUtils.fromDate(pollEntity.getEndDate()),
                    pollEntity.getIdentityId(),
                    pollEntity.getActivityId());
  }

  public static PollEntity toEntity(Poll poll) {
    if (poll == null) {
      return null;
    }
    PollEntity pollEntity = new PollEntity();
    pollEntity.setId(poll.getId());
    pollEntity.setPollQuestion(poll.getQuestion());
    pollEntity.setStartDate(RestUtils.toDate(poll.getStartDate()));
    pollEntity.setEndDate(RestUtils.toDate(poll.getEndDate()));
    pollEntity.setActivityId(poll.getActivityId());
    pollEntity.setIdentityId(poll.getCreatorId());
    return pollEntity;
  }

  public static PollOption optionFromEntity(PollOptionEntity pollOptionEntity) {
    if (pollOptionEntity == null) {
      return null;
    }
    return new PollOption(pollOptionEntity.getId(), pollOptionEntity.getPollId(), pollOptionEntity.getDescription());
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
