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

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

public class PollServiceTest extends BasePollTest {
  private Date            startDate  = new Date(1508484583259L);

  private Date            endDate    = new Date(1508484583260L);

  private final static String MESSAGE = "Activity title";

  @Test
  public void testCreatePoll() throws IllegalAccessException {
    // Given
    ZonedDateTime startDateTime = startDate.toInstant().atZone(ZoneOffset.UTC);
    ZonedDateTime endDateTime = endDate.toInstant().atZone(ZoneOffset.UTC);
    org.exoplatform.services.security.Identity identity = new org.exoplatform.services.security.Identity("testuser1");
      Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(startDateTime);
    poll.setEndDate(endDateTime);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    Poll createdPoll = new Poll(1, "q1", startDateTime, endDateTime, 1, 1);
    PollOption pollOption = new PollOption();
    pollOption.setDescription("pollOption");
    List<PollOption> options = new ArrayList<>();
    options.add(pollOption);
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);
    spaceService.addRedactor(space, user1Identity.getRemoteId());

    // When
    Poll pollStored = pollService.createPoll(poll, pollOptionList, space.getId(), MESSAGE, identity);

    assertNotNull(pollStored);
    assertEquals(createdPoll.getCreatorId(), pollStored.getCreatorId());
    assertEquals(createdPoll.getQuestion(), pollStored.getQuestion());

    // Given
    org.exoplatform.services.security.Identity identity1 = new org.exoplatform.services.security.Identity("testuser2");
    Poll poll1 = new Poll();
    poll1.setQuestion("q1");
    poll1.setCreatedDate(startDateTime);
    poll1.setEndDate(endDateTime);
    poll1.setCreatorId(Long.parseLong(user2Identity.getId()));

    // When
    try {
      pollService.createPoll(poll1, pollOptionList, space.getId(), MESSAGE, identity1);
      fail("Should fail when a non redactor member attempts to create a poll");
    } catch (IllegalAccessException e) {
      // Expected
    }
  }

  @Test
  public void testGetPollById() throws IllegalAccessException {
    ZonedDateTime startDateTime = startDate.toInstant().atZone(ZoneOffset.UTC);
    ZonedDateTime endDateTime = endDate.toInstant().atZone(ZoneOffset.UTC);
    org.exoplatform.services.security.Identity identity = new org.exoplatform.services.security.Identity("testuser1");
    Poll poll = new Poll();
    poll.setQuestion("q1");
    poll.setCreatedDate(startDateTime);
    poll.setEndDate(endDateTime);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    Poll createdPoll = new Poll(1, "q1", startDateTime, endDateTime, 1, 1);
    PollOption pollOption = new PollOption();
    pollOption.setDescription("pollOption");
    List<PollOption> options = new ArrayList<>();
    options.add(pollOption);
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);
    spaceService.addRedactor(space, user1Identity.getRemoteId());

    // When
    Poll pollStored = pollService.createPoll(poll, pollOptionList, space.getId(), identity);
    poll = pollService.getPollById(pollStored.getId());

    assertNotNull(poll);
    assertEquals(createdPoll.getQuestion(), poll.getQuestion());
    assertEquals(createdPoll.getCreatedDate(), poll.getCreatedDate());
    assertEquals(createdPoll.getEndDate(), poll.getEndDate());

    // When
    try {
      pollService.getPollById(5);
      fail("Should fail when poll doesn't exist");
    } catch (IllegalStateException e) {
      // Expected
    }
  }
}
