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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.service.BasePollTest;

public class PollUtilsTest extends BasePollTest {
  
  private Date                createdDate = new Date(1508484583259L);

  private Date                endDate     = new Date(11508484583260L);

  private final static String MESSAGE     = "Activity title";


  @Test
  public void testGetPollDuration() throws IllegalAccessException {
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
    long pollDuration = PollUtils.getPollDuration(createdPoll);

    // Then
    assertEquals(115740L, pollDuration);
  }
  
  @Test
  public void testToDate() throws ParseException {
    // Given
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
    String dateInString = "7-Jun-2013";
    Date date = formatter.parse(dateInString);
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    
    // When
    Date nullConvertedDate = PollUtils.toDate(null);
    Date convertedDate = PollUtils.toDate(zonedDateTime);
    
    // Then
    assertNull(nullConvertedDate);
    assertNotNull(convertedDate);
    assertEquals(date.getTime(), convertedDate.getTime());
  }
  
  @Test
  public void testGetCurrentUserIdentityId() {
    // When
    long user1IdentityId = PollUtils.getCurrentUserIdentityId(identityManager, "testuser1");
    
    // Then
    assertEquals(Long.parseLong(user1Identity.getId()), user1IdentityId);
  }
}
