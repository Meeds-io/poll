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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
    when(pollDAO.create(Mockito.any())).thenReturn(pollEntity);
    when(pollOptionDAO.create(Mockito.any())).thenReturn(pollOptionEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.toPollEntity(poll)).thenReturn(pollEntity);
    when(EntityMapper.fromPollEntity(pollEntity)).thenReturn(poll);
    when(EntityMapper.fromPollOptionEntity(pollOptionEntity)).thenReturn(pollOption);

    // When
    Poll pollCreated = pollStorage.createPoll(poll, Collections.singletonList(pollOption));

    // Then
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
    when(pollDAO.find(Mockito.any())).thenReturn(pollEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.fromPollEntity(pollEntity)).thenReturn(poll);
    
    // When
    Poll retrievedPoll = pollStorage.getPollById(poll.getId());

    // Then
    assertNotNull(retrievedPoll);
    assertEquals(retrievedPoll, poll);
  }
  
  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testGetPollOptionsById() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollEntity pollEntity = createPollEntity();
    PollOption pollOption = createPollOption(poll);
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);
    List<PollOptionEntity> pollOptionEntities = Arrays.asList( pollOptionEntity );
    when(pollOptionDAO.findPollOptionsById(Mockito.any())).thenReturn(pollOptionEntities);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.fromPollOptionEntity(pollOptionEntity)).thenReturn(pollOption);
    // When
    List<PollOption> retrievedPollOptions = pollStorage.getPollOptionsById(poll.getId());

    // Then
    assertNotNull(retrievedPollOptions);
    assertEquals(1L, retrievedPollOptions.get(0).getId());
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testUpdatePoll() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollEntity pollEntity = createPollEntity();
    poll.setActivityId(1L);
    when(pollDAO.update(Mockito.any())).thenReturn(pollEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.toPollEntity(poll)).thenReturn(pollEntity);
    when(EntityMapper.fromPollEntity(pollEntity)).thenReturn(poll);

    // When
    Poll updatedPoll = pollStorage.updatePoll(poll);

    // Then
    assertNotNull(updatedPoll);
    assertEquals(1L, updatedPoll.getActivityId());
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testCreatePollVote() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollOption pollOption = createPollOption(poll);
    PollVoteEntity pollVoteEntity = createPollVoteEntity(pollOption.getId());
    PollVote pollVote = createPollVote(pollOption.getId());
    when(pollVoteDAO.create(Mockito.any())).thenReturn(pollVoteEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.toPollVoteEntity(pollVote)).thenReturn(pollVoteEntity);
    when(EntityMapper.fromPollVoteEntity(pollVoteEntity)).thenReturn(pollVote);
    
    // When
    PollVote createdPollVote = pollStorage.createPollVote(pollVote);

    // Then
    assertNotNull(createdPollVote);
    assertEquals(1L, createdPollVote.getVoterId());
    assertEquals(1L, createdPollVote.getPollOptionId());
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testCountPollOptionTotalVotes() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollOption pollOption = createPollOption(poll);
    when(pollVoteDAO.countPollOptionTotalVotes(pollOption.getId())).thenReturn(1);
    
    // When
    int votes = pollStorage.countPollOptionTotalVotes(pollOption.getId());
    
    // Then
    assertEquals(1, votes);
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testCountPollOptionTotalVotesByUser() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollOption pollOption = createPollOption(poll);
    when(pollVoteDAO.countPollOptionTotalVotesByUser(pollOption.getId(), 1L)).thenReturn(1);
    
    // When
    int votes = pollStorage.countPollOptionTotalVotesByUser(pollOption.getId(), 1L);
    
    // Then
    assertEquals(1, votes);
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testGetPollOptionById() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollEntity pollEntity = createPollEntity();
    PollOption pollOption = createPollOption(poll);
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);
    when(pollOptionDAO.find(Mockito.any())).thenReturn(pollOptionEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.fromPollOptionEntity(pollOptionEntity)).thenReturn(pollOption);
    // When
    PollOption retrievedPollOption = pollStorage.getPollOptionById(poll.getId());

    // Then
    assertNotNull(retrievedPollOption);
    assertEquals(1L, retrievedPollOption.getId());
  }

  protected Poll createPoll() {
    Date createdDate = new Date(System.currentTimeMillis());
    Date endDate = new Date(11508484583260L);
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
  
  protected PollVote createPollVote(long optionId) {
    PollVote pollVote = new PollVote();
    pollVote.setId(1L);
    pollVote.setVoteDate(new Date());
    pollVote.setPollOptionId(optionId);
    pollVote.setVoterId(1L);
    return pollVote;
  }

  protected PollEntity createPollEntity() {
    Date createdDate = new Date(System.currentTimeMillis());
    Date endDate = new Date(11508484583260L);
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

  protected PollVoteEntity createPollVoteEntity(long optionId) {
    Date createdDate = new Date(System.currentTimeMillis());
    PollVoteEntity pollVoteEntity = new PollVoteEntity();
    pollVoteEntity.setPollOptionId(optionId);
    pollVoteEntity.setVoterId(1L);
    pollVoteEntity.setVoteDate(createdDate);
    return pollVoteEntity;
  }
}
