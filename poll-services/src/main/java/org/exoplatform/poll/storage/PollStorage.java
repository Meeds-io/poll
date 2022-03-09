/*
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2022 Meeds Association contact@meeds.io
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.poll.storage;

import java.util.List;
import java.util.stream.Collectors;

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

  public Poll createPoll(Poll poll, List<PollOption> pollOptions) {
    PollEntity pollEntity = EntityMapper.toPollEntity(poll);
    pollEntity = pollDAO.create(pollEntity);
    for (PollOption pollOption : pollOptions) {
      PollOptionEntity pollOptionEntity = EntityMapper.toPollOptionEntity(pollOption, pollEntity.getId());
      pollOptionDAO.create(pollOptionEntity);
    }
    return EntityMapper.fromPollEntity(pollEntity);
  }

  public Poll getPollById(Long pollId) {
    PollEntity pollEntity = pollDAO.find(pollId);
    return EntityMapper.fromPollEntity(pollEntity);
  }
  
  public List<PollOption> getPollOptionsById(Long pollId) {
    List<PollOptionEntity> pollOptionEntities = pollOptionDAO.findPollOptionsById(pollId);
    return pollOptionEntities.stream().map(EntityMapper::fromPollOptionEntity).collect(Collectors.toList());
  }

  public Poll updatePoll(Poll poll) {
    PollEntity pollEntity = EntityMapper.toPollEntity(poll);
    pollEntity = pollDAO.update(pollEntity);
    return EntityMapper.fromPollEntity(pollEntity);
  }

}
