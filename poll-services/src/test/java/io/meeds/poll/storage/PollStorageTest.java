/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.poll.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
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
import io.meeds.poll.entity.PollEntity;
import io.meeds.poll.entity.PollOptionEntity;
import io.meeds.poll.entity.PollVoteEntity;
import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.model.PollVote;
import io.meeds.spring.AvailableIntegration;

@ExtendWith({ SpringExtension.class, KernelExtension.class })
@SpringBootApplication(scanBasePackages = {
  BasePollTest.MODULE_NAME,
  AvailableIntegration.KERNEL_TEST_MODULE,
  AvailableIntegration.JPA_MODULE,
  AvailableIntegration.LIQUIBASE_MODULE,
})
@EnableJpaRepositories(basePackages = BasePollTest.MODULE_NAME)
@TestPropertySource(properties = {
  "spring.liquibase.change-log=" + BasePollTest.CHANGELOG_PATH,
})
public class PollStorageTest extends BasePollTest { // NOSONAR

  private static final long STORED_POLL_CREATOR_ID = 653L;

  private static final long STORED_VOTER_ID        = 225L;

  @Autowired
  private PollStorage       pollStorage;

  @Autowired
  private PollDAO           pollDAO;

  @Autowired
  private PollOptionDAO     pollOptionDAO;

  @Autowired
  private PollVoteDAO       pollVoteDAO;

  @AfterEach
  public void teardown() {
    pollVoteDAO.deleteAll();
    pollOptionDAO.deleteAll();
    pollDAO.deleteAll();
  }

  @Test
  public void createPoll() throws Exception { // NOSONAR
    // Given
    Poll poll = createPollInstance();
    PollOption pollOption = createPollOptionInstance(poll);

    // When
    Poll createdPoll = pollStorage.createPoll(poll, Collections.singletonList(pollOption));

    // Then
    assertNotNull(createdPoll);
    assertEquals(poll.getQuestion(), createdPoll.getQuestion());
    assertEquals(poll.getCreatedDate(), createdPoll.getCreatedDate());
    assertEquals(poll.getEndDate(), createdPoll.getEndDate());
    assertEquals(poll.getCreatorId(), createdPoll.getCreatorId());
  }

  @Test
  public void getPollById() throws Exception { // NOSONAR
    // Given
    PollEntity pollEntity = createPollEntity();

    // When
    Poll poll = pollStorage.getPollById(pollEntity.getId());

    // Then
    assertNotNull(poll);
    assertEquals(pollEntity.getId().longValue(), poll.getId());
    assertEquals(pollEntity.getQuestion(), poll.getQuestion());
    assertEquals(pollEntity.getCreatedDate(), poll.getCreatedDate());
    assertEquals(pollEntity.getEndDate(), poll.getEndDate());
    assertEquals(pollEntity.getCreatorId(), poll.getCreatorId());
  }

  @Test
  public void getPollOptionsByPollId() throws Exception { // NOSONAR
    PollEntity pollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);

    List<PollOption> retrievedPollOptions = pollStorage.getPollOptionsByPollId(pollEntity.getId());

    assertNotNull(retrievedPollOptions);
    assertEquals((long) pollOptionEntity.getId(), retrievedPollOptions.get(0).getId());
    assertEquals(pollOptionEntity.getDescription(), retrievedPollOptions.get(0).getDescription());
  }

  @Test
  public void updatePoll() throws Exception { // NOSONAR
    PollEntity pollEntity = createPollEntity();

    Poll poll = pollStorage.getPollById(pollEntity.getId());
    poll.setActivityId(55468l);

    Poll updatedPoll = pollStorage.updatePoll(poll);

    assertNotNull(updatedPoll);
    assertEquals(poll.getActivityId(), updatedPoll.getActivityId());

    updatedPoll = pollStorage.getPollById(pollEntity.getId());
    assertEquals(poll.getActivityId(), updatedPoll.getActivityId());
  }

  @Test
  public void createPollVote() throws Exception { // NOSONAR
    PollEntity pollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);
    PollVote pollVote = createPollVoteInstance(pollOptionEntity.getId());

    PollVote createdPollVote = pollStorage.createPollVote(pollVote);

    assertNotNull(createdPollVote);
    assertEquals(pollVote.getVoterId(), createdPollVote.getVoterId());
    assertEquals(pollVote.getPollOptionId(), createdPollVote.getPollOptionId());
  }

  @Test
  public void countPollOptionTotalVotes() throws Exception { // NOSONAR
    PollEntity pollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);

    assertEquals(0, pollStorage.countPollOptionTotalVotes(pollOptionEntity.getId()));

    pollStorage.createPollVote(new PollVote(pollOptionEntity.getId(), 1l, new Date()));
    assertEquals(1, pollStorage.countPollOptionTotalVotes(pollOptionEntity.getId()));

    assertThrows(Exception.class, () -> pollStorage.createPollVote(new PollVote(pollOptionEntity.getId(), 1l, new Date())));
    assertEquals(1, pollStorage.countPollOptionTotalVotes(pollOptionEntity.getId()));

    pollStorage.createPollVote(new PollVote(pollOptionEntity.getId(), 2l, new Date()));
    assertEquals(2, pollStorage.countPollOptionTotalVotes(pollOptionEntity.getId()));
  }

  @Test
  public void countPollOptionTotalVotesByUser() throws Exception { // NOSONAR
    PollEntity pollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);

    assertEquals(0, pollStorage.countPollOptionTotalVotesByUser(pollOptionEntity.getId(), 1l));
    assertEquals(0, pollStorage.countPollOptionTotalVotesByUser(pollOptionEntity.getId(), 2l));

    pollStorage.createPollVote(new PollVote(pollOptionEntity.getId(), 1l, new Date()));
    assertEquals(1, pollStorage.countPollOptionTotalVotesByUser(pollOptionEntity.getId(), 1l));
    assertEquals(0, pollStorage.countPollOptionTotalVotesByUser(pollOptionEntity.getId(), 2l));

    assertThrows(Exception.class, () -> pollStorage.createPollVote(new PollVote(pollOptionEntity.getId(), 1l, new Date())));
    assertEquals(1, pollStorage.countPollOptionTotalVotesByUser(pollOptionEntity.getId(), 1l));
    assertEquals(0, pollStorage.countPollOptionTotalVotesByUser(pollOptionEntity.getId(), 2l));

    pollStorage.createPollVote(new PollVote(pollOptionEntity.getId(), 2l, new Date()));
    assertEquals(1, pollStorage.countPollOptionTotalVotesByUser(pollOptionEntity.getId(), 1l));
    assertEquals(1, pollStorage.countPollOptionTotalVotesByUser(pollOptionEntity.getId(), 2l));
  }

  @Test
  public void getPollOptionById() throws Exception { // NOSONAR
    PollEntity pollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);

    // When
    PollOption retrievedPollOption = pollStorage.getPollOptionById(pollOptionEntity.getId());

    // Then
    assertNotNull(retrievedPollOption);
    assertEquals((long) pollOptionEntity.getId(), retrievedPollOption.getId());
    assertEquals((long) pollEntity.getId(), retrievedPollOption.getPollId());
    assertEquals(pollOptionEntity.getDescription(), retrievedPollOption.getDescription());
  }

  @Test
  public void countPollOptionsByPollId() throws Exception { // NOSONAR
    PollEntity pollEntity = createPollEntity();

    createPollOptionEntity(pollEntity);
    assertEquals(1, pollStorage.countPollOptionsByPollId(pollEntity.getId()));

    createPollOptionEntity(pollEntity);
    assertEquals(2, pollStorage.countPollOptionsByPollId(pollEntity.getId()));
  }

  @Test
  public void countPollTotalVotes() throws Exception { // NOSONAR
    // NOSONAR
    PollEntity pollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);

    assertEquals(0, pollStorage.countPollTotalVotes(pollEntity.getId()));

    pollStorage.createPollVote(new PollVote(pollOptionEntity.getId(), 1l, new Date()));
    assertEquals(1, pollStorage.countPollTotalVotes(pollEntity.getId()));

    assertThrows(Exception.class, () -> pollStorage.createPollVote(new PollVote(pollOptionEntity.getId(), 1l, new Date())));
    assertEquals(1, pollStorage.countPollTotalVotes(pollEntity.getId()));

    pollStorage.createPollVote(new PollVote(pollOptionEntity.getId(), 2l, new Date()));
    assertEquals(2, pollStorage.countPollTotalVotes(pollEntity.getId()));
  }

  @Test
  public void didVote() throws Exception { // NOSONAR
    // Given
    PollEntity pollEntity = createPollEntity();
    PollOptionEntity pollOptionEntity = createPollOptionEntity(pollEntity);

    boolean didVote = pollStorage.didVote(STORED_VOTER_ID, pollEntity.getId());
    assertFalse(didVote);

    createPollVoteEntity(pollOptionEntity);
    didVote = pollStorage.didVote(STORED_VOTER_ID, pollEntity.getId());
    assertTrue(didVote);
  }

  protected Poll createPollInstance() {
    Date createdDate = new Date(System.currentTimeMillis());
    Date endDate = new Date(11508484583260L);
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(1L);
    return poll;
  }

  protected PollOption createPollOptionInstance(Poll poll) {
    PollOption pollOption = new PollOption();
    pollOption.setPollId(poll.getId());
    pollOption.setDescription("pollOption description");
    return pollOption;
  }

  protected PollVote createPollVoteInstance(long optionId) {
    PollVote pollVote = new PollVote();
    pollVote.setVoteDate(new Date());
    pollVote.setPollOptionId(optionId);
    pollVote.setVoterId(8554L);
    return pollVote;
  }

  protected PollEntity createPollEntity() {
    Date createdDate = new Date(System.currentTimeMillis());
    Date endDate = new Date(11508484583260L);
    PollEntity pollEntity = new PollEntity();
    pollEntity.setQuestion("q" + System.currentTimeMillis());
    pollEntity.setCreatedDate(createdDate);
    pollEntity.setEndDate(endDate);
    pollEntity.setCreatorId(STORED_POLL_CREATOR_ID);
    return pollDAO.save(pollEntity);
  }

  protected PollOptionEntity createPollOptionEntity(PollEntity pollEntity) {
    PollOptionEntity pollOptionEntity = new PollOptionEntity();
    pollOptionEntity.setPoll(pollEntity);
    pollOptionEntity.setDescription("pollOption description");
    return pollOptionDAO.save(pollOptionEntity);
  }

  protected PollVoteEntity createPollVoteEntity(PollOptionEntity pollOptionEntity) {
    Date createdDate = new Date(System.currentTimeMillis());
    PollVoteEntity pollVoteEntity = new PollVoteEntity();
    pollVoteEntity.setPollOption(pollOptionEntity);
    pollVoteEntity.setVoterId(STORED_VOTER_ID);
    pollVoteEntity.setVoteDate(createdDate);
    return pollVoteDAO.save(pollVoteEntity);
  }

}
