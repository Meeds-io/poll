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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.exoplatform.poll.dao.PollDAO;
import org.exoplatform.poll.dao.PollOptionDAO;
import org.exoplatform.poll.dao.PollVoteDAO;
import org.exoplatform.poll.entity.PollEntity;
import org.exoplatform.poll.entity.PollOptionEntity;
import org.exoplatform.poll.entity.PollVoteEntity;
import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.model.PollVote;
import org.exoplatform.poll.utils.EntityMapper;

public class PollStorage {
  private PollDAO       pollDAO;

  private PollOptionDAO pollOptionDAO;

  private PollVoteDAO pollVoteDAO;


  public PollStorage(PollDAO pollDAO, PollOptionDAO pollOptionDAO, PollVoteDAO pollVoteDAO) {
    this.pollDAO = pollDAO;
    this.pollOptionDAO = pollOptionDAO;
    this.pollVoteDAO = pollVoteDAO;
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

  public PollVote addVote(PollVote pollVote) {
    PollVoteEntity pollVoteEntity = EntityMapper.toPollVoteEntity(pollVote);
    pollVoteEntity = pollVoteDAO.create(pollVoteEntity);
    return EntityMapper.fromPollVoteEntity(pollVoteEntity);
  }

  public List<Integer> getPollVotesById(Long pollId) {
    List<PollOptionEntity> pollOptionEntities = pollOptionDAO.findPollOptionsById(pollId);
    List<Integer> optionsTotalVotes = new ArrayList<>();
    for (PollOptionEntity pollOptionEntity : pollOptionEntities) {
      optionsTotalVotes.add(pollVoteDAO.getTotalVotesByOption(pollOptionEntity.getId()));
    }
    return optionsTotalVotes;
  }

  public List<Boolean> checkVoted(Long pollId, Long userId) {
    List<PollOptionEntity> pollOptionEntities = pollOptionDAO.findPollOptionsById(pollId);
    List<Boolean> optionsVoted = new ArrayList<>();
    for (PollOptionEntity pollOptionEntity : pollOptionEntities) {
      optionsVoted.add(pollVoteDAO.checkVoted(pollOptionEntity.getId(), userId));
    }
    return optionsVoted;
  }

  public PollOption getPollOptionById(Long pollOptionId) {
    PollOptionEntity pollOptionEntity = pollOptionDAO.find(pollOptionId);
    return EntityMapper.fromPollOptionEntity(pollOptionEntity);
  }


}
