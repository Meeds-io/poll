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
package org.exoplatform.poll.storage;

import org.exoplatform.poll.dao.PollDAO;
import org.exoplatform.poll.dao.PollOptionDAO;
import org.exoplatform.poll.entity.PollEntity;
import org.exoplatform.poll.entity.PollOptionEntity;
import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.utils.EntityMapper;
import org.exoplatform.poll.utils.RestUtils;

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
    pollEntity.setStartDate(RestUtils.toDate(poll.getStartDate()));
    pollEntity.setEndDate(RestUtils.toDate(poll.getEndDate()));
    pollEntity = pollDAO.create(pollEntity);
    return EntityMapper.fromEntity(pollEntity);
  }

  public PollOption createPollOption(PollOption pollOption) {
    PollOptionEntity pollOptionEntity = EntityMapper.optionToEntity(pollOption);
    pollOptionEntity.setId(null);
    pollOptionEntity.setDescription(pollOption.getDescription());
    pollOptionEntity = pollOptionDAO.create(pollOptionEntity);
    return EntityMapper.optionFromEntity(pollOptionEntity);
  }
}
