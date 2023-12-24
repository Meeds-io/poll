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
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.poll;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.exoplatform.commons.testing.BaseExoContainerTestSuite;
import org.exoplatform.commons.testing.ConfigTestCase;

import io.meeds.poll.dao.PollDAOTest;
import io.meeds.poll.dao.PollOptionDAOTest;
import io.meeds.poll.dao.PollVoteDAOTest;
import io.meeds.poll.listener.GamificationPollListenerTest;
import io.meeds.poll.service.PollServiceTest;
import io.meeds.poll.storage.PollStorageTest;
import io.meeds.poll.utils.PollUtilsTest;
import io.meeds.poll.utils.RestEntityBuilderTest;

@RunWith(Suite.class)
@SuiteClasses({
  PollDAOTest.class,
  PollOptionDAOTest.class,
  PollVoteDAOTest.class,
  GamificationPollListenerTest.class,
  PollStorageTest.class,
  PollServiceTest.class,
  PollUtilsTest.class,
  RestEntityBuilderTest.class,
})
@ConfigTestCase(BasePollTest.class)
public class InitContainerTestSuite extends BaseExoContainerTestSuite {

  @BeforeClass
  public static void setUp() throws Exception {
    initConfiguration(InitContainerTestSuite.class);
    beforeSetup();
  }

  @AfterClass
  public static void tearDown() {
    afterTearDown();
  }

}
