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
package io.meeds.poll.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.rest.model.PollOptionRestEntity;
import io.meeds.poll.rest.model.PollRestEntity;
import io.meeds.poll.service.BasePollTest;

public class RestEntityBuilderTest extends BasePollTest {
  
  private Date                createdDate = new Date(1508484583259L);

  private Date                endDate     = new Date(11508484583260L);

  @Test
  public void testFromPoll() {
    // Given
    Poll poll = new Poll();
    poll.setId(1);
    poll.setQuestion("q1");
    poll.setCreatedDate(createdDate);
    poll.setEndDate(endDate);
    poll.setCreatorId(Long.parseLong(user1Identity.getId()));
    poll.setSpaceId(1L);
    PollOptionRestEntity pollOptionRestEntity = new PollOptionRestEntity();
    pollOptionRestEntity.setDescription("pollOption");
    List<PollOptionRestEntity> pollOptionRestEntities = new ArrayList<>();
    pollOptionRestEntities.add(pollOptionRestEntity);

    // When
    PollRestEntity pollRestEntity = RestEntityBuilder.fromPoll(poll, pollOptionRestEntities);

    // Then
    assertEquals(1, pollRestEntity.getId());
  }
  
  @Test
  public void testFromPollOption() {
    // Given
    PollOption pollOption = new PollOption();
    pollOption.setId(1);
    pollOption.setDescription("pollOption");

    // When
    PollOptionRestEntity pollOptionRestEntity = RestEntityBuilder.fromPollOption(pollOption, 2, true);

    // Then
    assertEquals(1, pollOptionRestEntity.getId());
  }
  
  @Test
  public void testToPoll() {
    // Given
    PollRestEntity pollRestEntity = new PollRestEntity();
    pollRestEntity.setQuestion("q1");
    pollRestEntity.setDuration("");

    // When
    try {
      RestEntityBuilder.toPoll(pollRestEntity);
      fail("Should fail when a poll has an unexpected duration value");
    } catch (IllegalStateException e) {
      // Expected
    }
    
    // Given
    pollRestEntity.setDuration("1day");
    
    // When
    Poll poll = RestEntityBuilder.toPoll(pollRestEntity);

    // Then
    assertEquals("q1", poll.getQuestion());
  }
  
  @Test
  public void testToPollOptions() {
    // Given
    PollOptionRestEntity pollOptionRestEntity = new PollOptionRestEntity();
    pollOptionRestEntity.setDescription("pollOption");
    List<PollOptionRestEntity> pollOptionRestEntities = new ArrayList<>();
    pollOptionRestEntities.add(pollOptionRestEntity);

    // When
    List<PollOption> options = RestEntityBuilder.toPollOptions(pollOptionRestEntities);

    // Then
    assertEquals(1, options.size());
    assertEquals("pollOption", options.get(0).getDescription());
  }
}
