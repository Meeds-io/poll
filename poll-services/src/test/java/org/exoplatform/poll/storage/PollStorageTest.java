/*
 * Copyright (C) 2022 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.poll.storage;

import org.exoplatform.poll.dao.PollDAO;
import org.exoplatform.poll.dao.PollOptionDAO;
import org.exoplatform.poll.entity.PollEntity;
import org.exoplatform.poll.entity.PollOptionEntity;
import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.utils.EntityMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.xml.*", "org.xml.*" })
public class PollStorageTest {
  private PollStorage pollStorage;

  private PollDAO     pollDAO;
  private PollOptionDAO     pollOptionDAO;

  @Before
  public void setUp() throws Exception { // NOSONAR
    pollDAO = mock(PollDAO.class);
    pollOptionDAO = mock(PollOptionDAO.class);
    pollStorage = new PollStorage(pollDAO, pollOptionDAO);
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testCreatePoll() throws Exception { // NOSONAR
      Date startDate = new Date(System.currentTimeMillis());
      Date endDate = new Date(System.currentTimeMillis() + 1);
      Poll poll = new Poll();
      poll.setId(1L);
      poll.setQuestion("q1");
      poll.setStartDate(startDate.toInstant().atZone(ZoneOffset.UTC));
      poll.setEndDate(endDate.toInstant().atZone(ZoneOffset.UTC));
      poll.setCreatorId(1L);

      PollOption pollOption = new PollOption();
      pollOption.setId(1L);
      pollOption.setPollId(poll.getId());
      pollOption.setPollOption("pollOption");


      PollEntity pollEntity = new PollEntity();
      pollEntity.setId(1L);
      pollEntity.setPollQuestion("q1");
      pollEntity.setStart_date(startDate);
      pollEntity.setEnd_date(endDate);
      pollEntity.setIdentityId(1L);

      PollOptionEntity pollOptionEntity = new PollOptionEntity();
      pollOptionEntity.setPollId(pollEntity.getId());
      pollOptionEntity.setPollOption(pollOption.getPollOption());
      pollOptionEntity.setId(1L);

      when(pollDAO.create(anyObject())).thenReturn(pollEntity);
      when(pollOptionDAO.create(anyObject())).thenReturn(pollOptionEntity);
      PowerMockito.mockStatic(EntityMapper.class);
      when(EntityMapper.toEntity(poll)).thenReturn(pollEntity);
      when(EntityMapper.fromEntity(pollEntity)).thenReturn(poll);
      when(EntityMapper.optionToEntity(pollOption)).thenReturn(pollOptionEntity);
      when(EntityMapper.optionFromEntity(pollOptionEntity)).thenReturn(pollOption);
      PollOption pollOptionCreated = pollStorage.createPollOption(pollOption);

      assertNotNull(pollOptionCreated);
      assertEquals(pollOptionCreated.getId(), 1l);
      assertEquals(pollOption, pollOptionCreated);

      Poll pollCreated = pollStorage.createPoll(poll);
      assertNotNull(pollCreated);
      assertEquals(pollCreated.getId(), 1l);
      assertEquals(poll, pollCreated);
  }
}
