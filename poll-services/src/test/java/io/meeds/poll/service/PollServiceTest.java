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
package io.meeds.poll.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.model.PollVote;

import static org.junit.Assert.*;

public class PollServiceTest extends BasePollTest {
  private Date                createdDate = new Date(1508484583259L);

  private Date                endDate     = new Date(11508484583260L);

  private final static String MESSAGE     = "Activity title";

  @Test
  public void testCreatePoll() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    PollOption pollOption = new PollOption();
    pollOption.setDescription("pollOption");
    List<PollOption> options = new ArrayList<>();
    options.add(pollOption);
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);
    spaceService.addRedactor(space, user1Identity.getRemoteId());
    // When
    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    
    // Then
    assertNotNull(createdPoll);
    assertEquals(Long.parseLong(user1Identity.getId()), createdPoll.getCreatorId());
    assertEquals("q1", createdPoll.getQuestion());
    assertNotEquals(poll.getActivityId(), createdPoll.getActivityId());

    // Given
    org.exoplatform.services.security.Identity testuser2Identity = new org.exoplatform.services.security.Identity("testuser2");
    Poll poll1 = new Poll();
    poll1.setQuestion("q1");
    poll1.setCreatedDate(createdDate);
    poll1.setEndDate(endDate);
    poll1.setCreatorId(Long.parseLong(user2Identity.getId()));
    poll1.setSpaceId(1L);

    // When
    try {
      pollService.createPoll(poll1, pollOptionList, space.getId(), MESSAGE, testuser2Identity, new ArrayList<>());
      fail("Should fail when a non redactor member attempts to create a poll");
    } catch (IllegalAccessException e) {
      // Expected
    }
  }

  @Test
  public void testGetPollById() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription("pollOption");
    List<PollOption> options = new ArrayList<>();
    options.add(pollOption);
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);
    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());

    // When
    Poll retrievedPoll = pollService.getPollById(createdPoll.getId(), testUser1Identity);

    // Then
    assertNotNull(poll);
    assertEquals("q1", retrievedPoll.getQuestion());
    assertEquals(createdDate, retrievedPoll.getCreatedDate());
    assertEquals(endDate, retrievedPoll.getEndDate());
    
    // When
    try {
      org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity("testuser3");
      pollService.getPollById(retrievedPoll.getId(), testUser3Identity);
      fail("Should fail when a non member attempts to get the poll " + retrievedPoll.getId());
    } catch (IllegalAccessException e) {
      // Expected
    }
  }
  
  @Test
  public void testGetPollOptionById() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription("pollOption");
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);
    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    List<PollOption> createdPollOptions = pollService.getPollOptionsByPollId(createdPoll.getId(), testUser1Identity);

    // When
    PollOption retrievedPollOption = pollService.getPollOptionById(createdPollOptions.get(0).getId(), testUser1Identity);

    // Then
    assertNotNull(retrievedPollOption);
    assertEquals("pollOption", retrievedPollOption.getDescription());
    
 // When
    try {
      org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity("testuser3");
      pollService.getPollOptionById(createdPollOptions.get(0).getId(), testUser3Identity);
      fail("Should fail when a non member attempts to get the poll option " +  retrievedPollOption.getId());
    } catch (IllegalAccessException e) {
      // Expected
    }
  }

  @Test
  public void testGetPollOptionsByPollId() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription("pollOption");
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);
    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());

    // When
    List<PollOption> retrievedPollOptions = pollService.getPollOptionsByPollId(createdPoll.getId(), testUser1Identity);

    // Then
    assertNotNull(retrievedPollOptions);
    assertEquals(1, retrievedPollOptions.size());
    
    // When
    try {
      org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity("testuser3");
      pollService.getPollOptionsByPollId(createdPoll.getId(), testUser3Identity);
      fail("Should fail when a non member attempts to get poll " + createdPoll.getId() + " options");
    } catch (IllegalAccessException e) {
      // Expected
    }
  }
  
  @Test
  public void testVote() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription("pollOption");
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);

    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    List<PollOption> createdPollOptions = pollService.getPollOptionsByPollId(createdPoll.getId(), testUser1Identity);

    // When
    PollVote voteAdded = pollService.vote(String.valueOf(createdPollOptions.get(0).getId()), space.getId(), testUser1Identity);

    // Then
    assertNotNull(voteAdded);
    assertEquals(createdPollOptions.get(0).getId(), voteAdded.getPollOptionId());
    assertEquals(Long.parseLong(user1Identity.getId()), voteAdded.getVoterId());
    
    // When
    try {
      org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity("testuser3");
      pollService.vote(String.valueOf(createdPollOptions.get(0).getId()), space.getId(), testUser3Identity);
      fail("Should fail when a non member attempts to vote in poll " + createdPoll.getId());
    } catch (IllegalAccessException e) {
      // Expected
    }
  }
  
  @Test
  public void testExpiredVote() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(createdDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription("pollOption");
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);

    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    List<PollOption> createdPollOptions = pollService.getPollOptionsByPollId(createdPoll.getId(), testUser1Identity);

    // When
    try {
      pollService.vote(String.valueOf(createdPollOptions.get(0).getId()), space.getId(), testUser1Identity);
      fail("Should fail when a member attempts to vote in expired poll " + createdPoll.getId());
    } catch (IllegalAccessException e) {
      // Expected
    }
  }
  
  @Test
  public void testGetPollOptionTotalVotes() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    org.exoplatform.services.security.Identity testUser2Identity = new org.exoplatform.services.security.Identity("testuser2");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption1 = new PollOption();
    pollOption1.setDescription("pollOption1");
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption1);
    PollOption pollOption2 = new PollOption();
    pollOption2.setDescription("pollOption2");
    pollOptionList.add(pollOption2);

    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    List<PollOption> createdPollOptions = pollService.getPollOptionsByPollId(createdPoll.getId(), testUser1Identity);
    pollService.vote(String.valueOf(createdPollOptions.get(0).getId()), space.getId(), testUser1Identity);
    pollService.vote(String.valueOf(createdPollOptions.get(0).getId()), space.getId(), testUser2Identity);
    pollService.vote(String.valueOf(createdPollOptions.get(1).getId()), space.getId(), testUser2Identity);

    // When
    int pollOption1Votes = pollService.getPollOptionTotalVotes(createdPollOptions.get(0).getId(), space.getId(), testUser1Identity);
    int pollOption2Votes = pollService.getPollOptionTotalVotes(createdPollOptions.get(1).getId(), space.getId(), testUser1Identity);

    // Then
    assertEquals(2, pollOption1Votes);
    assertEquals(1, pollOption2Votes);
    
    // When
    try {
      org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity("testuser3");
      pollService.getPollOptionTotalVotes(createdPollOptions.get(0).getId(), space.getId(), testUser3Identity);
      fail("Should fail when a non member attempts to get the poll option " +  createdPollOptions.get(0).getId() + " total votes");
    } catch (IllegalAccessException e) {
      // Expected
    }
  }
  
  @Test
  public void testIsPollOptionVoted() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    org.exoplatform.services.security.Identity testUser2Identity = new org.exoplatform.services.security.Identity("testuser2");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption1 = new PollOption();
    pollOption1.setDescription("pollOption1");
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption1);
    PollOption pollOption2 = new PollOption();
    pollOption2.setDescription("pollOption2");
    pollOptionList.add(pollOption2);

    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    List<PollOption> createdPollOptions = pollService.getPollOptionsByPollId(createdPoll.getId(), testUser1Identity);
    pollService.vote(String.valueOf(createdPollOptions.get(0).getId()), space.getId(), testUser1Identity);
    pollService.vote(String.valueOf(createdPollOptions.get(0).getId()), space.getId(), testUser2Identity);
    pollService.vote(String.valueOf(createdPollOptions.get(1).getId()), space.getId(), testUser2Identity);

    // When
    boolean isPollOption1VotedByUser1 = pollService.isPollOptionVoted(createdPollOptions.get(0).getId(), space.getId(), testUser1Identity);
    boolean isPollOption2VotedByUser1 = pollService.isPollOptionVoted(createdPollOptions.get(1).getId(), space.getId(), testUser1Identity);
    boolean isPollOption1VotedByUser2 = pollService.isPollOptionVoted(createdPollOptions.get(0).getId(), space.getId(), testUser2Identity);
    boolean isPollOption2VotedByUser2 = pollService.isPollOptionVoted(createdPollOptions.get(1).getId(), space.getId(), testUser2Identity);

    // Then
    assertEquals(true, isPollOption1VotedByUser1);
    assertEquals(false, isPollOption2VotedByUser1);
    assertEquals(true, isPollOption1VotedByUser2);
    assertEquals(true, isPollOption2VotedByUser2);
    
    // When
    try {
      org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity("testuser3");
      pollService.isPollOptionVoted(createdPollOptions.get(0).getId(), space.getId(), testUser3Identity);
      fail("Should fail when a non member attempts to check if the poll option " +  createdPollOptions.get(0).getId() + " is voted");
    } catch (IllegalAccessException e) {
      // Expected
    }
  }

  @Test
  public void testGetPollOptionsNumber() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption1 = new PollOption();
    pollOption1.setDescription("pollOption");
    PollOption pollOption2 = new PollOption();
    pollOption2.setDescription("pollOption");
    PollOption pollOption3 = new PollOption();
    pollOption3.setDescription("pollOption");
    PollOption pollOption4 = new PollOption();
    pollOption4.setDescription("pollOption");
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption1);
    pollOptionList.add(pollOption2);
    pollOptionList.add(pollOption3);
    pollOptionList.add(pollOption4);

    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());

    // When
    int pollOptionsNumber = pollService.getPollOptionsNumber(createdPoll.getId(), testUser1Identity);

    // Then
    assertEquals(4, pollOptionsNumber);
  }

  @Test
  public void testGetPollTotalVotes() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    org.exoplatform.services.security.Identity testUser2Identity = new org.exoplatform.services.security.Identity("testuser2");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption1 = new PollOption();
    pollOption1.setDescription("pollOption");
    PollOption pollOption2 = new PollOption();
    pollOption2.setDescription("pollOption");
    PollOption pollOption3 = new PollOption();
    pollOption3.setDescription("pollOption");
    PollOption pollOption4 = new PollOption();
    pollOption4.setDescription("pollOption");
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption1);
    pollOptionList.add(pollOption2);
    pollOptionList.add(pollOption3);
    pollOptionList.add(pollOption4);
    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    List<PollOption> createdPollOptions = pollService.getPollOptionsByPollId(createdPoll.getId(), testUser1Identity);
    pollService.vote(String.valueOf(createdPollOptions.get(0).getId()), space.getId(), testUser1Identity);
    pollService.vote(String.valueOf(createdPollOptions.get(0).getId()), space.getId(), testUser2Identity);
    pollService.vote(String.valueOf(createdPollOptions.get(1).getId()), space.getId(), testUser2Identity);

    // When
    int pollTotalVotes = pollService.getPollTotalVotes(createdPoll.getId(), testUser1Identity);

    // Then
    assertEquals(3, pollTotalVotes);
  }
}
