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
package org.exoplatform.poll.dao;

import java.util.Date;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.poll.entity.PollEntity;
import org.exoplatform.poll.entity.PollOptionEntity;
import org.exoplatform.poll.entity.PollVoteEntity;
import org.exoplatform.services.naming.InitialContextInitializer;

import junit.framework.TestCase;

public class PollVoteDAOTest extends TestCase {

  private Date            startDate   = new Date(1508484583259L);

  private Date            endDate     = new Date(11508484583260L);

  private String          question    = "q1";

  private String          description = "pollOption description";

  private Long            creatorId   = 1L;

  private Long            activityId  = 0L;

  private Long            spaceId     = 1L;

  private PortalContainer container;

  private PollOptionDAO   pollOptionDAO;

  private PollDAO         pollDAO;

  private PollVoteDAO     pollVoteDAO;

  @Override
  protected void setUp() throws Exception {
    RootContainer rootContainer = RootContainer.getInstance();
    rootContainer.getComponentInstanceOfType(InitialContextInitializer.class);

    container = PortalContainer.getInstance();
    pollOptionDAO = container.getComponentInstanceOfType(PollOptionDAO.class);
    pollDAO = container.getComponentInstanceOfType(PollDAO.class);
    pollVoteDAO = container.getComponentInstanceOfType(PollVoteDAO.class);
    ExoContainerContext.setCurrentContainer(container);
    begin();
  }

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
    int votes = pollVoteDAO.countPollOptionTotalVotes(pollOptionEntity.getId());

    // Then
    assertEquals(1, votes);
  }

  public void testCountPollOptionTotalVotesByUser() {
    // Given
    PollEntity createdPollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(createdPollEntity.getId());
    PollVoteEntity pollVoteEntity = createPollVoteEntity(pollOptionEntity.getId());
    pollVoteDAO.create(pollVoteEntity);

    // When
    int votes = pollVoteDAO.countPollOptionTotalVotesByUser(pollOptionEntity.getId(), creatorId);

    // Then
    assertEquals(1, votes);
  }

  protected PollEntity createPollEntity() {
    PollEntity pollEntity = new PollEntity();
    pollEntity.setQuestion(question);
    pollEntity.setCreatedDate(startDate);
    pollEntity.setEndDate(endDate);
    pollEntity.setCreatorId(creatorId);
    pollEntity.setActivityId(activityId);
    pollEntity.setSpaceId(spaceId);
    PollEntity createdPollEntity = pollDAO.create(pollEntity);
    return createdPollEntity;
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

  @Override
  protected void tearDown() throws Exception {
    end();
  }

  private void begin() {
    RequestLifeCycle.begin(container);
  }

  private void end() {
    RequestLifeCycle.end();
  }
}
