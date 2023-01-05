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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

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

@RunWith(MockitoJUnitRunner.class)
public class PollStorageTest {

  private static MockedStatic<EntityMapper> entityMapper;

  private PollStorage                       pollStorage;

  private PollDAO                           pollDAO;

  private PollOptionDAO                     pollOptionDAO;

  private PollVoteDAO                       pollVoteDAO;

  @BeforeClass
  public static void beforeClass() throws Exception { // NOSONAR
    entityMapper = mockStatic(EntityMapper.class);
  }

  @AfterClass
  public static void afterClass() throws Exception { // NOSONAR
    entityMapper.close();
  }

  @Before
  public void setUp() throws Exception { // NOSONAR
    pollDAO = mock(PollDAO.class);
    pollOptionDAO = mock(PollOptionDAO.class);
    pollVoteDAO = mock(PollVoteDAO.class);
    pollStorage = new PollStorage(pollDAO, pollOptionDAO, pollVoteDAO);
  }

  @Test
  public void testCreatePoll() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollOption pollOption = createPollOption(poll);
    PollEntity pollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);
    when(pollDAO.create(Mockito.any())).thenReturn(pollEntity);
    when(pollOptionDAO.create(Mockito.any())).thenReturn(pollOptionEntity);
    entityMapper.when(() -> EntityMapper.toPollEntity(poll)).thenReturn(pollEntity);
    entityMapper.when(() -> EntityMapper.fromPollEntity(pollEntity)).thenReturn(poll);
    entityMapper.when(() -> EntityMapper.fromPollOptionEntity(pollOptionEntity)).thenReturn(pollOption);

    // When
    Poll createdPoll = pollStorage.createPoll(poll, Collections.singletonList(pollOption));

    // Then
    assertNotNull(createdPoll);
    assertEquals(1L, createdPoll.getId());
    assertEquals("q1", createdPoll.getQuestion());
  }

  @Test
  public void testGetPollById() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollEntity pollEntity = createPollEntity();
    when(pollDAO.find(Mockito.any())).thenReturn(pollEntity);
    entityMapper.when(() -> EntityMapper.fromPollEntity(pollEntity)).thenReturn(poll);

    // When
    Poll retrievedPoll = pollStorage.getPollById(poll.getId());

    // Then
    assertNotNull(retrievedPoll);
    assertEquals(1L, retrievedPoll.getId());
    assertEquals("q1", retrievedPoll.getQuestion());
  }

  @Test
  public void testGetPollOptionsByPollId() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollEntity pollEntity = createPollEntity();
    PollOption pollOption = createPollOption(poll);
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);
    List<PollOptionEntity> pollOptionEntities = Arrays.asList(pollOptionEntity);
    when(pollOptionDAO.findPollOptionsByPollId(Mockito.any())).thenReturn(pollOptionEntities);
    entityMapper.when(() -> EntityMapper.fromPollOptionEntity(pollOptionEntity)).thenReturn(pollOption);
    // When
    List<PollOption> retrievedPollOptions = pollStorage.getPollOptionsByPollId(poll.getId());

    // Then
    assertNotNull(retrievedPollOptions);
    assertEquals(1L, retrievedPollOptions.get(0).getId());
  }

  @Test
  public void testUpdatePoll() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollEntity pollEntity = createPollEntity();
    poll.setActivityId(1L);
    when(pollDAO.update(Mockito.any())).thenReturn(pollEntity);
    entityMapper.when(() -> EntityMapper.toPollEntity(poll)).thenReturn(pollEntity);
    entityMapper.when(() -> EntityMapper.fromPollEntity(pollEntity)).thenReturn(poll);

    // When
    Poll updatedPoll = pollStorage.updatePoll(poll);

    // Then
    assertNotNull(updatedPoll);
    assertEquals(1L, updatedPoll.getActivityId());
  }

  @Test
  public void testCreatePollVote() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollOption pollOption = createPollOption(poll);
    PollVoteEntity pollVoteEntity = createPollVoteEntity(pollOption.getId());
    PollVote pollVote = createPollVote(pollOption.getId());
    when(pollVoteDAO.create(Mockito.any())).thenReturn(pollVoteEntity);
    entityMapper.when(() -> EntityMapper.toPollVoteEntity(pollVote)).thenReturn(pollVoteEntity);
    entityMapper.when(() -> EntityMapper.fromPollVoteEntity(pollVoteEntity)).thenReturn(pollVote);

    // When
    PollVote createdPollVote = pollStorage.createPollVote(pollVote);

    // Then
    assertNotNull(createdPollVote);
    assertEquals(1L, createdPollVote.getVoterId());
    assertEquals(1L, createdPollVote.getPollOptionId());
  }

  @Test
  public void testCountPollOptionTotalVotes() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollOption pollOption = createPollOption(poll);
    when(pollVoteDAO.countPollOptionTotalVotes(pollOption.getId())).thenReturn(1);

    // When
    int pollOptionTotalVotes = pollStorage.countPollOptionTotalVotes(pollOption.getId());

    // Then
    assertEquals(1, pollOptionTotalVotes);
  }

  @Test
  public void testCountPollOptionTotalVotesByUser() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollOption pollOption = createPollOption(poll);
    when(pollVoteDAO.countPollOptionTotalVotesByUser(pollOption.getId(), 1L)).thenReturn(1);

    // When
    int pollOptionTotalVotesByUser = pollStorage.countPollOptionTotalVotesByUser(pollOption.getId(), 1L);

    // Then
    assertEquals(1, pollOptionTotalVotesByUser);
  }

  @Test
  public void testGetPollOptionById() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    PollEntity pollEntity = createPollEntity();
    PollOption pollOption = createPollOption(poll);
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);
    when(pollOptionDAO.find(Mockito.any())).thenReturn(pollOptionEntity);
    entityMapper.when(() -> EntityMapper.fromPollOptionEntity(pollOptionEntity)).thenReturn(pollOption);
    // When
    PollOption retrievedPollOption = pollStorage.getPollOptionById(poll.getId());

    // Then
    assertNotNull(retrievedPollOption);
    assertEquals(1L, retrievedPollOption.getId());
  }

  @Test
  public void testCountPollOptionsByPollId() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    createPollOption(poll);
    when(pollOptionDAO.countPollOptionsByPollId(poll.getId())).thenReturn(1);

    // When
    int pollOptionsNumber = pollStorage.countPollOptionsByPollId(poll.getId());

    // Then
    assertEquals(1, pollOptionsNumber);
  }

  @Test
  public void testCountPollTotalVotes() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    createPollOption(poll);
    when(pollVoteDAO.countPollTotalVotes(poll.getId())).thenReturn(1);

    // When
    int pollTotalVotes = pollStorage.countPollTotalVotes(poll.getId());

    // Then
    assertEquals(1, pollTotalVotes);
  }

  @Test
  public void testDidVote() throws Exception { // NOSONAR
    // Given
    Poll poll = createPoll();
    createPollOption(poll);
    when(pollVoteDAO.countUserVotesInPoll(poll.getId(), 1L)).thenReturn(1L);

    // When
    boolean didVote = pollStorage.didVote(1L, poll.getId());

    // Then
    assertTrue(didVote);
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
