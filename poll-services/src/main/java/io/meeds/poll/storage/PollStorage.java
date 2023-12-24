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
package io.meeds.poll.storage;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.meeds.poll.dao.PollDAO;
import io.meeds.poll.dao.PollOptionDAO;
import io.meeds.poll.dao.PollVoteDAO;
import io.meeds.poll.entity.PollEntity;
import io.meeds.poll.entity.PollOptionEntity;
import io.meeds.poll.entity.PollVoteEntity;
import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.model.PollVote;
import io.meeds.poll.utils.EntityMapper;

@Service
public class PollStorage {
  private PollDAO       pollDAO;

  private PollOptionDAO pollOptionDAO;

  private PollVoteDAO   pollVoteDAO;

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

  public Poll getPollById(long pollId) {
    PollEntity pollEntity = pollDAO.find(pollId);
    return EntityMapper.fromPollEntity(pollEntity);
  }

  public List<PollOption> getPollOptionsByPollId(long pollId) {
    List<PollOptionEntity> pollOptionEntities = pollOptionDAO.findPollOptionsByPollId(pollId);
    return pollOptionEntities.stream().map(EntityMapper::fromPollOptionEntity).collect(Collectors.toList());
  }

  public Poll updatePoll(Poll poll) {
    PollEntity pollEntity = EntityMapper.toPollEntity(poll);
    pollEntity = pollDAO.update(pollEntity);
    return EntityMapper.fromPollEntity(pollEntity);
  }

  public PollVote createPollVote(PollVote pollVote) {
    PollVoteEntity pollVoteEntity = EntityMapper.toPollVoteEntity(pollVote);
    pollVoteEntity = pollVoteDAO.create(pollVoteEntity);
    return EntityMapper.fromPollVoteEntity(pollVoteEntity);
  }

  public int countPollOptionTotalVotes(long pollOptionId) {
    return pollVoteDAO.countPollOptionTotalVotes(pollOptionId);
  }

  public int countPollOptionTotalVotesByUser(long pollOptionId, long userId) {
    return pollVoteDAO.countPollOptionTotalVotesByUser(pollOptionId, userId);
  }

  public PollOption getPollOptionById(long pollOptionId) {
    PollOptionEntity pollOptionEntity = pollOptionDAO.find(pollOptionId);
    return EntityMapper.fromPollOptionEntity(pollOptionEntity);
  }

  public int countPollOptionsByPollId(long pollId) {
    return pollOptionDAO.countPollOptionsByPollId(pollId);
  }

  public int countPollTotalVotes(long pollId) {
    return pollVoteDAO.countPollTotalVotes(pollId);
  }

  public boolean didVote(long currentUserIdentityId, Long pollId) {
    return pollVoteDAO.countUserVotesInPoll(pollId, currentUserIdentityId) > 0;
  }
}
