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
package org.exoplatform.poll.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.exoplatform.poll.model.PollVote;
import org.junit.Test;

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;

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
    assertEquals(poll.getCreatorId(), createdPoll.getCreatorId());
    assertEquals(poll.getQuestion(), createdPoll.getQuestion());
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

    // When
    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    createdPoll = pollService.getPollById(createdPoll.getId(), testUser1Identity);

    // Then
    assertNotNull(poll);
    assertEquals(poll.getQuestion(), createdPoll.getQuestion());
    assertEquals(poll.getCreatedDate(), createdPoll.getCreatedDate());
    assertEquals(poll.getEndDate(), createdPoll.getEndDate());
    
    // When
    try {
      org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity("testuser3");
      pollService.getPollById(createdPoll.getId(), testUser3Identity);
      fail("Should fail when a non member attempts to get a poll");
    } catch (IllegalAccessException e) {
      // Expected
    }
  }

  @Test
  public void testGetPollOptionsById() throws IllegalAccessException {
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

    // When
    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    List<PollOption> retrievedPollOptions = pollService.getPollOptionsById(createdPoll.getId(), testUser1Identity);

    // Then
    assertNotNull(retrievedPollOptions);
    assertEquals(1, retrievedPollOptions.size());
  }

  @Test
  public void testAddVote() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(new Date(11508484583260L));
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription("pollOption");
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);

    PollVote pollVote = new PollVote();
    pollVote.setPollOptionId(1L);
    pollVote.setVoteDate(createdDate);
    pollVote.setVoterId(Long.parseLong(user1Identity.getId()));

    // When
    pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    PollVote voteAdded = pollService.addVote(pollVote, space.getId(), testUser1Identity);

    // Then
    assertNotNull(voteAdded);
    assertEquals(pollVote.getVoteDate(), voteAdded.getVoteDate());
    assertEquals(pollVote.getPollOptionId(), voteAdded.getPollOptionId());
    assertEquals(pollVote.getVoterId(), voteAdded.getVoterId());

  }

  @Test
  public void testGetPollVotesById() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(new Date(11508484583260L));
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription("pollOption");
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);

    PollVote pollVote = new PollVote();
    pollVote.setVoteDate(createdDate);
    pollVote.setVoterId(Long.parseLong(user1Identity.getId()));
    pollVote.setPollOptionId(1L);

    // When
    pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    pollService.addVote(pollVote, space.getId(), testUser1Identity);
    List<Integer> votes = pollService.getPollVotesById(1L, testUser1Identity);

    // Then
    assertNotNull(votes);
    assertEquals(1, votes.size());
  }

  @Test
  public void testCheckVoted() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity("testuser1");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(new Date(11508484583260L));
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription("pollOption");
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);

    PollVote pollVote = new PollVote();
    pollVote.setVoteDate(createdDate);
    pollVote.setVoterId(Long.parseLong(user1Identity.getId()));
    pollVote.setPollOptionId(1L);

    // When
    pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    pollService.addVote(pollVote, space.getId(), testUser1Identity);
    List<Boolean> voted = pollService.checkVoted(1L, testUser1Identity);

    // Then
    assertNotNull(voted);
    assertEquals(1, voted.size());
    assertEquals(true, voted.get(0));

  }
}
