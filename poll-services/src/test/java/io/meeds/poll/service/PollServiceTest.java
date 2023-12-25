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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.meeds.kernel.test.KernelExtension;
import io.meeds.poll.BasePollTest;
import io.meeds.poll.dao.PollDAO;
import io.meeds.poll.dao.PollOptionDAO;
import io.meeds.poll.dao.PollVoteDAO;
import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.model.PollVote;
import io.meeds.spring.AvailableIntegration;

@ExtendWith({ SpringExtension.class, KernelExtension.class })
@SpringBootApplication(scanBasePackages = {
  BasePollTest.MODULE_NAME,
  AvailableIntegration.KERNEL_MODULE,
  AvailableIntegration.JPA_MODULE,
  AvailableIntegration.LIQUIBASE_MODULE,
})
@EnableJpaRepositories(basePackages = BasePollTest.MODULE_NAME)
@TestPropertySource(properties = {
  "spring.liquibase.change-log=" + BasePollTest.CHANGELOG_PATH,
})
public class PollServiceTest extends BasePollTest { // NOSONAR

  private static final String MESSAGE                 = "Activity title";

  private static final String POLL_OPTION_DESCRIPTION = "pollOption";

  @Autowired
  private PollService         pollService;

  @Autowired
  private PollDAO             pollDAO;

  @Autowired
  private PollOptionDAO       pollOptionDAO;

  @Autowired
  private PollVoteDAO         pollVoteDAO;

  private Date                createdDate             = new Date(1508484583259L);

  private Date                endDate                 = new Date(11508484583260L);

  @AfterEach
  @Override
  public void afterEach() {
    pollVoteDAO.deleteAll();
    pollOptionDAO.deleteAll();
    pollDAO.deleteAll();
    super.afterEach();
  }

  @Test
  public void createPoll() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity(TESTUSER_1);
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    PollOption pollOption = new PollOption();
    pollOption.setDescription(POLL_OPTION_DESCRIPTION);
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
    org.exoplatform.services.security.Identity testuser2Identity = new org.exoplatform.services.security.Identity(TESTUSER_2);
    Poll poll1 = new Poll();
    poll1.setQuestion("q1");
    poll1.setCreatedDate(createdDate);
    poll1.setEndDate(endDate);
    poll1.setCreatorId(Long.parseLong(user2Identity.getId()));
    poll1.setSpaceId(1L);

    // When
    assertThrows(IllegalAccessException.class, () -> pollService.createPoll(poll1, pollOptionList, space.getId(), MESSAGE, testuser2Identity, new ArrayList<>()));
  }

  @Test
  public void getPollById() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity(TESTUSER_1);
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription(POLL_OPTION_DESCRIPTION);
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
      org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity(TESTUSER_3);
      assertThrows(IllegalAccessException.class, () -> pollService.getPollById(retrievedPoll.getId(), testUser3Identity));
  }

  @Test
  public void getPollOptionById() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity(TESTUSER_1);
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription(POLL_OPTION_DESCRIPTION);
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);
    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    List<PollOption> createdPollOptions = pollService.getPollOptionsByPollId(createdPoll.getId(), testUser1Identity);

    // When
    PollOption retrievedPollOption = pollService.getPollOptionById(createdPollOptions.get(0).getId(), testUser1Identity);

    // Then
    assertNotNull(retrievedPollOption);
    assertEquals(POLL_OPTION_DESCRIPTION, retrievedPollOption.getDescription());

    // When
    org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity(TESTUSER_3);
    assertThrows(IllegalAccessException.class, () -> pollService.getPollOptionById(createdPollOptions.get(0).getId(), testUser3Identity));
  }

  @Test
  public void getPollOptionsByPollId() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity(TESTUSER_1);
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription(POLL_OPTION_DESCRIPTION);
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);
    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());

    // When
    List<PollOption> retrievedPollOptions = pollService.getPollOptionsByPollId(createdPoll.getId(), testUser1Identity);

    // Then
    assertNotNull(retrievedPollOptions);
    assertEquals(1, retrievedPollOptions.size());

    // When
    org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity(TESTUSER_3);
    assertThrows(IllegalAccessException.class, () -> pollService.getPollOptionsByPollId(createdPoll.getId(), testUser3Identity));
  }

  @Test
  public void voteOnPoll() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity(TESTUSER_1);
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription(POLL_OPTION_DESCRIPTION);
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
    org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity(TESTUSER_3);
    assertThrows(IllegalAccessException.class, () -> pollService.vote(String.valueOf(createdPollOptions.get(0).getId()), space.getId(), testUser3Identity));
  }

  @Test
  public void voteOnExpiredPoll() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity(TESTUSER_1);
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(createdDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption = new PollOption();
    pollOption.setDescription(POLL_OPTION_DESCRIPTION);
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);

    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    List<PollOption> createdPollOptions = pollService.getPollOptionsByPollId(createdPoll.getId(), testUser1Identity);

    // When
    assertThrows(IllegalAccessException.class, () -> pollService.vote(String.valueOf(createdPollOptions.get(0).getId()), space.getId(), testUser1Identity));
  }

  @Test
  public void getPollOptionTotalVotes() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity(TESTUSER_1);
    org.exoplatform.services.security.Identity testUser2Identity = new org.exoplatform.services.security.Identity(TESTUSER_2);
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
    int pollOption1Votes =
                         pollService.getPollOptionTotalVotes(createdPollOptions.get(0).getId(), space.getId(), testUser1Identity);
    int pollOption2Votes =
                         pollService.getPollOptionTotalVotes(createdPollOptions.get(1).getId(), space.getId(), testUser1Identity);

    // Then
    assertEquals(2, pollOption1Votes);
    assertEquals(1, pollOption2Votes);

    // When
    org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity(TESTUSER_3);
    assertThrows(IllegalAccessException.class, () -> pollService.getPollOptionTotalVotes(createdPollOptions.get(0).getId(), space.getId(), testUser3Identity));
  }

  @Test
  public void isPollOptionVoted() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity(TESTUSER_1);
    org.exoplatform.services.security.Identity testUser2Identity = new org.exoplatform.services.security.Identity(TESTUSER_2);
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
    boolean isPollOption1VotedByUser1 = pollService.isPollOptionVoted(createdPollOptions.get(0).getId(),
                                                                      space.getId(),
                                                                      testUser1Identity);
    boolean isPollOption2VotedByUser1 = pollService.isPollOptionVoted(createdPollOptions.get(1).getId(),
                                                                      space.getId(),
                                                                      testUser1Identity);
    boolean isPollOption1VotedByUser2 = pollService.isPollOptionVoted(createdPollOptions.get(0).getId(),
                                                                      space.getId(),
                                                                      testUser2Identity);
    boolean isPollOption2VotedByUser2 = pollService.isPollOptionVoted(createdPollOptions.get(1).getId(),
                                                                      space.getId(),
                                                                      testUser2Identity);

    // Then
    assertEquals(true, isPollOption1VotedByUser1);
    assertEquals(false, isPollOption2VotedByUser1);
    assertEquals(true, isPollOption1VotedByUser2);
    assertEquals(true, isPollOption2VotedByUser2);

    // When
    org.exoplatform.services.security.Identity testUser3Identity = new org.exoplatform.services.security.Identity(TESTUSER_3);
    assertThrows(IllegalAccessException.class, () -> pollService.isPollOptionVoted(createdPollOptions.get(0).getId(), space.getId(), testUser3Identity));
  }

  @Test
  public void getPollOptionsNumber() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity(TESTUSER_1);
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption1 = new PollOption();
    pollOption1.setDescription(POLL_OPTION_DESCRIPTION);
    PollOption pollOption2 = new PollOption();
    pollOption2.setDescription(POLL_OPTION_DESCRIPTION);
    PollOption pollOption3 = new PollOption();
    pollOption3.setDescription(POLL_OPTION_DESCRIPTION);
    PollOption pollOption4 = new PollOption();
    pollOption4.setDescription(POLL_OPTION_DESCRIPTION);
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
  public void getPollTotalVotes() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity(TESTUSER_1);
    org.exoplatform.services.security.Identity testUser2Identity = new org.exoplatform.services.security.Identity(TESTUSER_2);
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption1 = new PollOption();
    pollOption1.setDescription(POLL_OPTION_DESCRIPTION);
    PollOption pollOption2 = new PollOption();
    pollOption2.setDescription(POLL_OPTION_DESCRIPTION);
    PollOption pollOption3 = new PollOption();
    pollOption3.setDescription(POLL_OPTION_DESCRIPTION);
    PollOption pollOption4 = new PollOption();
    pollOption4.setDescription(POLL_OPTION_DESCRIPTION);
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

  @Test
  public void didVote() throws IllegalAccessException {
    // Given
    org.exoplatform.services.security.Identity testUser1Identity = new org.exoplatform.services.security.Identity(TESTUSER_1);
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOption pollOption1 = new PollOption();
    pollOption1.setDescription(POLL_OPTION_DESCRIPTION);
    PollOption pollOption2 = new PollOption();
    pollOption2.setDescription(POLL_OPTION_DESCRIPTION);
    PollOption pollOption3 = new PollOption();
    pollOption3.setDescription(POLL_OPTION_DESCRIPTION);
    PollOption pollOption4 = new PollOption();
    pollOption4.setDescription(POLL_OPTION_DESCRIPTION);
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption1);
    pollOptionList.add(pollOption2);
    pollOptionList.add(pollOption3);
    pollOptionList.add(pollOption4);
    Poll createdPoll = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, testUser1Identity, new ArrayList<>());
    List<PollOption> createdPollOptions = pollService.getPollOptionsByPollId(createdPoll.getId(), testUser1Identity);
    // When
    boolean didVote = pollService.didVote(testUser1Identity, createdPoll.getId());

    // Then
    assertFalse(didVote);

    pollService.vote(String.valueOf(createdPollOptions.get(0).getId()), space.getId(), testUser1Identity);
    // When
    didVote = pollService.didVote(testUser1Identity, createdPoll.getId());

    // Then
    assertTrue(didVote);
  }
}
