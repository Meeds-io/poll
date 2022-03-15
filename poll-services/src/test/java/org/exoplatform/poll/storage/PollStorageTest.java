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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.exoplatform.poll.dao.PollVoteDAO;
import org.exoplatform.poll.entity.PollVoteEntity;
import org.exoplatform.poll.model.PollVote;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.exoplatform.poll.dao.PollDAO;
import org.exoplatform.poll.dao.PollOptionDAO;
import org.exoplatform.poll.entity.PollEntity;
import org.exoplatform.poll.entity.PollOptionEntity;
import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.utils.EntityMapper;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.xml.*", "org.xml.*" })
public class PollStorageTest {
  private PollStorage   pollStorage;

  private PollDAO       pollDAO;

  private PollOptionDAO pollOptionDAO;

  private PollVoteDAO   pollVoteDAO;

  @Before
  public void setUp() throws Exception { // NOSONAR
    pollDAO = mock(PollDAO.class);
    pollOptionDAO = mock(PollOptionDAO.class);
    pollVoteDAO = mock(PollVoteDAO.class);
    pollStorage = new PollStorage(pollDAO, pollOptionDAO, pollVoteDAO);
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testCreatePoll() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollOption pollOption = createPollOption(poll);
    PollEntity pollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);

    // When
    when(pollDAO.create(anyObject())).thenReturn(pollEntity);
    when(pollOptionDAO.create(anyObject())).thenReturn(pollOptionEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.toPollEntity(poll)).thenReturn(pollEntity);
    when(EntityMapper.fromPollEntity(pollEntity)).thenReturn(poll);
    when(EntityMapper.toPollOptionEntity(pollOption, pollEntity.getId())).thenReturn(pollOptionEntity);
    when(EntityMapper.fromPollOptionEntity(pollOptionEntity)).thenReturn(pollOption);

    // Then
    Poll pollCreated = pollStorage.createPoll(poll, Collections.singletonList(pollOption));
    assertNotNull(pollCreated);
    assertEquals(1L, pollCreated.getId());
    assertEquals(poll, pollCreated);
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testGetPollById() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollEntity pollEntity = createPollEntity();
    
    // When
    when(pollDAO.find(anyObject())).thenReturn(pollEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.toPollEntity(poll)).thenReturn(pollEntity);
    when(EntityMapper.fromPollEntity(pollEntity)).thenReturn(poll);

    // Then
    Poll retrievedPoll = pollStorage.getPollById(poll.getId());
    assertNotNull(retrievedPoll);
    assertEquals(retrievedPoll, poll);
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testUpdatePoll() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollEntity pollEntity = createPollEntity();
    poll.setActivityId(1L);

    // When
    when(pollDAO.update(anyObject())).thenReturn(pollEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.toPollEntity(poll)).thenReturn(pollEntity);
    when(EntityMapper.fromPollEntity(pollEntity)).thenReturn(poll);

    // Then
    Poll updatedPoll = pollStorage.updatePoll(poll);
    assertNotNull(updatedPoll);
    assertEquals(1L, updatedPoll.getActivityId());
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testAddVote() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollOption pollOption = createPollOption(poll);
    PollVoteEntity pollVoteEntity = createPollVoteEntity(pollOption.getId());
    PollVote pollVote = createPollVote(pollOption.getId());

    // When
    when(pollVoteDAO.create(anyObject())).thenReturn(pollVoteEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.toPollVoteEntity(pollVote)).thenReturn(pollVoteEntity);
    when(EntityMapper.fromPollVoteEntity(pollVoteEntity)).thenReturn(pollVote);

    // Then
    PollVote voteAdded = pollStorage.addVote(pollVote);
    assertNotNull(voteAdded);
    assertNotNull(voteAdded.getId());
    assertEquals(1L, voteAdded.getVoterId());
    assertEquals(1L, voteAdded.getPollOptionId());
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testGetPollVotesById() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollOption pollOption = createPollOption(poll);
    PollVoteEntity pollVoteEntity = createPollVoteEntity(pollOption.getId());
    PollVote pollVote = createPollVote(pollOption.getId());

    // When
    when(pollVoteDAO.create(anyObject())).thenReturn(pollVoteEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.toPollVoteEntity(pollVote)).thenReturn(pollVoteEntity);
    when(EntityMapper.fromPollVoteEntity(pollVoteEntity)).thenReturn(pollVote);

    // Then
    PollVote voteAdded = pollStorage.addVote(pollVote);

    // When
    when(pollVoteDAO.getTotalVotesByOption(anyObject())).thenReturn(1);
    PowerMockito.mockStatic(EntityMapper.class);

    // Then
    List<Integer> votes = pollStorage.getPollVotesById(poll.getId());
    assertNotNull(voteAdded);
    assertNotNull(voteAdded.getId());
    assertEquals(1L, voteAdded.getVoterId());
    assertEquals(1L, voteAdded.getPollOptionId());
    assertNotNull(votes);
    assertNotNull(votes.size());

  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testCheckVoted() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollOption pollOption = createPollOption(poll);
    PollVoteEntity pollVoteEntity = createPollVoteEntity(pollOption.getId());
    PollVote pollVote = createPollVote(pollOption.getId());

    // When
    when(pollVoteDAO.create(anyObject())).thenReturn(pollVoteEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.toPollVoteEntity(pollVote)).thenReturn(pollVoteEntity);
    when(EntityMapper.fromPollVoteEntity(pollVoteEntity)).thenReturn(pollVote);

    // Then
    PollVote voteAdded = pollStorage.addVote(pollVote);
    List<Boolean> voted = pollStorage.checkVoted(poll.getId(), 1L);
    assertNotNull(voteAdded);
    assertNotNull(voteAdded.getId());
    assertEquals(1L, voteAdded.getVoterId());
    assertEquals(1L, voteAdded.getPollOptionId());
    assertNotNull(voted);
    assertNotNull(voted.size());
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testGetPollOptionById() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollEntity pollEntity = createPollEntity();
    PollOption pollOption = createPollOption(poll);
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);

    // When
    when(pollOptionDAO.find(anyObject())).thenReturn(pollOptionEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.toPollOptionEntity(pollOption, pollEntity.getId())).thenReturn(pollOptionEntity);
    when(EntityMapper.fromPollOptionEntity(pollOptionEntity)).thenReturn(pollOption);

    // Then
    PollOption retrievedPollOption = pollStorage.getPollOptionById(poll.getId());
    assertNotNull(retrievedPollOption);
    assertEquals(retrievedPollOption, pollOption);
  }

  protected Poll createPoll() {
    Date createdDate = new Date(System.currentTimeMillis());
    Date endDate = new Date(System.currentTimeMillis() + 1);
    Poll poll = new Poll();
    poll.setId(1L);
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(1L);
    return poll;
  }

  protected PollOption createPollOption(Poll poll) {
    PollOption pollOption = new PollOption();
    pollOption.setId(1L);
    pollOption.setPollId(poll.getId());
    pollOption.setDescription("pollOption description");
    return pollOption;
  }

  protected PollEntity createPollEntity() {
    Date createdDate = new Date(System.currentTimeMillis());
    Date endDate = new Date(System.currentTimeMillis() + 1);
    PollEntity pollEntity = new PollEntity();
    pollEntity.setId(1L);
    pollEntity.setQuestion("q1");
    pollEntity.setCreatedDate(createdDate);
    pollEntity.setEndDate(endDate);
    pollEntity.setCreatorId(1L);
    return pollEntity;
  }

  protected PollOptionEntity createPollOptionEntity(PollEntity pollEntity) {
    PollOptionEntity pollOptionEntity = new PollOptionEntity();
    pollOptionEntity.setPollId(pollEntity.getId());
    pollOptionEntity.setDescription("pollOption description");
    pollOptionEntity.setId(1L);
    return pollOptionEntity;
  }

  protected PollVoteEntity createPollVoteEntity(Long optionId) {
    Date createdDate = new Date(System.currentTimeMillis());
    PollVoteEntity pollVoteEntity = new PollVoteEntity();
    pollVoteEntity.setPollOptionId(optionId);
    pollVoteEntity.setVoterId(1L);
    pollVoteEntity.setVoteDate(createdDate);
    return pollVoteEntity;
  }

  protected PollVote createPollVote(Long optionId) {
    PollVote pollVote = new PollVote();
    pollVote.setVoteDate(new Date());
    pollVote.setPollOptionId(optionId);
    pollVote.setVoterId(1L);
    pollVote.setId(1L);
    return pollVote;
  }
}
