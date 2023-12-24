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
package io.meeds.poll.dao;

import java.util.Date;

import io.meeds.poll.BasePollTest;
import io.meeds.poll.entity.PollEntity;
import io.meeds.poll.entity.PollOptionEntity;
import io.meeds.poll.entity.PollVoteEntity;

public class PollVoteDAOTest extends BasePollTest { // NOSONAR

  private Date   startDate   = new Date(1508484583259L);

  private Date   endDate     = new Date(11508484583260L);

  private String question    = "q1";

  private String description = "pollOption description";

  private Long   creatorId   = 1L;

  private Long   activityId  = 0L;

  private Long   spaceId     = 1L;

  public void testCreateVote() {
    // Given
    PollEntity createdPollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(createdPollEntity.getId());
    PollVoteEntity pollVoteEntity = createPollVoteEntity(pollOptionEntity.getId());
    // When
    pollVoteEntity = pollVoteDAO.create(pollVoteEntity);

    // Then
    assertNotNull(pollVoteEntity);
    assertNotNull(pollVoteEntity.getId());
    assertEquals(pollOptionEntity.getId(), pollVoteEntity.getPollOptionId());
    assertEquals(startDate, pollVoteEntity.getVoteDate());
    assertEquals(creatorId, pollVoteEntity.getVoterId());
  }

  public void testCountPollOptionTotalVotes() {
    // Given
    PollEntity createdPollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(createdPollEntity.getId());
    PollVoteEntity pollVoteEntity = createPollVoteEntity(pollOptionEntity.getId());
    pollVoteDAO.create(pollVoteEntity);

    // When
    int pollOptionTotalVotes = pollVoteDAO.countPollOptionTotalVotes(pollOptionEntity.getId());

    // Then
    assertEquals(1, pollOptionTotalVotes);
  }

  public void testCountPollOptionTotalVotesByUser() {
    // Given
    PollEntity createdPollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(createdPollEntity.getId());
    PollVoteEntity pollVoteEntity = createPollVoteEntity(pollOptionEntity.getId());
    pollVoteDAO.create(pollVoteEntity);

    // When
    int pollOptionTotalVotesByUser = pollVoteDAO.countPollOptionTotalVotesByUser(pollOptionEntity.getId(), creatorId);

    // Then
    assertEquals(1, pollOptionTotalVotesByUser);
  }

  public void testCountPollTotalVotes() {
    // Given
    PollEntity createdPollEntity = createPollEntity();
    PollOptionEntity pollOption1Entity = createPollOptionEntity(createdPollEntity.getId());
    PollVoteEntity pollVote1Entity = createPollVoteEntity(pollOption1Entity.getId());
    pollVoteDAO.create(pollVote1Entity);
    PollOptionEntity pollOption2Entity = createPollOptionEntity(createdPollEntity.getId());
    PollVoteEntity pollVote2Entity = createPollVoteEntity(pollOption2Entity.getId());
    pollVoteDAO.create(pollVote2Entity);

    // When
    int pollTotalVotes = pollVoteDAO.countPollTotalVotes(createdPollEntity.getId());

    // Then
    assertEquals(2, pollTotalVotes);
  }

  public void testCountUserVotesInPollOptions() {
    PollEntity createdPollEntity = createPollEntity();
    PollOptionEntity pollOption1Entity = createPollOptionEntity(createdPollEntity.getId());
    PollVoteEntity pollVote1Entity = createPollVoteEntity(pollOption1Entity.getId());
    pollVoteDAO.create(pollVote1Entity);
    assertEquals(1, pollVoteDAO.countUserVotesInPoll(createdPollEntity.getId(), creatorId));
  }

  protected PollEntity createPollEntity() {
    PollEntity pollEntity = new PollEntity();
    pollEntity.setQuestion(question);
    pollEntity.setCreatedDate(startDate);
    pollEntity.setEndDate(endDate);
    pollEntity.setCreatorId(creatorId);
    pollEntity.setActivityId(activityId);
    pollEntity.setSpaceId(spaceId);
    return pollDAO.create(pollEntity);
  }

  protected PollOptionEntity createPollOptionEntity(long pollId) {
    PollOptionEntity pollOptionEntity = new PollOptionEntity();
    pollOptionEntity.setPollId(pollId);
    pollOptionEntity.setDescription(description);
    return pollOptionDAO.create(pollOptionEntity);
  }

  protected PollVoteEntity createPollVoteEntity(long optionId) {
    PollVoteEntity pollVoteEntity = new PollVoteEntity();
    pollVoteEntity.setPollOptionId(optionId);
    pollVoteEntity.setVoterId(creatorId);
    pollVoteEntity.setVoteDate(startDate);
    return pollVoteEntity;
  }

}
