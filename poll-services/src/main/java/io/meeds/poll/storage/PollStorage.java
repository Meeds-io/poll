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

import org.springframework.stereotype.Repository;

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

@Repository
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
    pollEntity = pollDAO.save(pollEntity);
    for (PollOption pollOption : pollOptions) {
      PollOptionEntity pollOptionEntity = EntityMapper.toPollOptionEntity(pollOption, pollEntity);
      pollOptionDAO.save(pollOptionEntity);
    }
    return EntityMapper.fromPollEntity(pollEntity);
  }

  public Poll getPollById(long pollId) {
    PollEntity pollEntity = pollDAO.findById(pollId).orElse(null);
    return EntityMapper.fromPollEntity(pollEntity);
  }

  public List<PollOption> getPollOptionsByPollId(long pollId) {
    List<PollOptionEntity> pollOptionEntities = pollOptionDAO.findByPollId(pollId);
    return pollOptionEntities.stream().map(po -> EntityMapper.fromPollOptionEntity(po, pollId)).toList();
  }

  public Poll updatePoll(Poll poll) {
    PollEntity pollEntity = EntityMapper.toPollEntity(poll);
    pollEntity = pollDAO.save(pollEntity);
    return EntityMapper.fromPollEntity(pollEntity);
  }

  public PollVote createPollVote(PollVote pollVote) {
    PollOptionEntity pollOptionEntity = pollOptionDAO.findById(pollVote.getPollOptionId()).orElseThrow();
    PollVoteEntity pollVoteEntity = EntityMapper.toPollVoteEntity(pollVote, pollOptionEntity);
    pollVoteEntity = pollVoteDAO.save(pollVoteEntity);
    return EntityMapper.fromPollVoteEntity(pollVoteEntity, pollVote.getPollOptionId());
  }

  public int countPollOptionTotalVotes(long pollOptionId) {
    return pollVoteDAO.countByPollOptionId(pollOptionId);
  }

  public int countPollOptionTotalVotesByUser(long pollOptionId, long userId) {
    return pollVoteDAO.countByPollOptionIdAndVoterId(pollOptionId, userId);
  }

  public PollOption getPollOptionById(long pollOptionId) {
    PollOptionEntity pollOptionEntity = pollOptionDAO.findById(pollOptionId).orElse(null);
    return EntityMapper.fromPollOptionEntity(pollOptionEntity);
  }

  public int countPollOptionsByPollId(long pollId) {
    return pollOptionDAO.countByPollId(pollId);
  }

  public int countPollTotalVotes(long pollId) {
    return pollVoteDAO.countByPollId(pollId);
  }

  public boolean didVote(long currentUserIdentityId, Long pollId) {
    return pollVoteDAO.countByPollIdAndVoterId(pollId, currentUserIdentityId) > 0;
  }

}
