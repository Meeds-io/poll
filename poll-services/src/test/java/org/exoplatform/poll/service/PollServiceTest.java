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

import org.exoplatform.poll.storage.PollStorage;
import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PollServiceTest {
  private PollService pollService;

  private PollStorage pollStorage;

  @Before
  public void setUp() throws Exception { // NOSONAR
    pollStorage = mock(PollStorage.class);
    pollService = new PollServiceImpl(pollStorage);
  }

  @Test
  public void testCreatePoll() throws IllegalAccessException {
    // Given
    ZonedDateTime startDate = new Date(System.currentTimeMillis()).toInstant().atZone(ZoneOffset.UTC);
    ZonedDateTime endDate = new Date(System.currentTimeMillis() + 1).toInstant().atZone(ZoneOffset.UTC);
    Poll poll = new Poll(0, "q1", startDate, endDate, 1, 1);
    Poll createdPoll = new Poll(1, "q1", startDate, endDate, 1, 1);
    PollOption pollOption = new PollOption(0, 0, "pollOption");
    List<PollOption> options = new ArrayList<>();
    options.add(pollOption);
    List<PollOption> pollOptionList = new ArrayList<>();
    pollOptionList.add(pollOption);
    when(pollStorage.createPoll(poll, options)).thenReturn(createdPoll);

    // When
    Poll poll1 = pollService.createPoll(poll, pollOptionList, 1);

    assertNotNull(poll1);
    assertEquals(createdPoll.getId(), poll1.getId());
    assertEquals(createdPoll.getQuestion(), poll1.getQuestion());
  }
}
