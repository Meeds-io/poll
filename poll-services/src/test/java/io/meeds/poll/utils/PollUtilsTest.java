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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.meeds.kernel.test.KernelExtension;
import io.meeds.poll.BasePollTest;
import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.service.PollService;
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
public class PollUtilsTest extends BasePollTest { // NOSONAR

  private static final String MESSAGE     = "Activity title";

  private Date                createdDate = new Date(1508484583259L);

  private Date                endDate     = new Date(11508484583260L);

  @Autowired
  private PollService   pollService;

  @Test
  public void getPollDuration() throws IllegalAccessException {
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
  public void formatToDate() throws ParseException {
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
  public void getUserIdentityId() {
    // When
    long user1IdentityId = PollUtils.getUserIdentityId(identityManager, "testuser1");

    // Then
    assertEquals(Long.parseLong(user1Identity.getId()), user1IdentityId);
  }

}
