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
package org.exoplatform.poll.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.exoplatform.poll.dao.PollDAO;
import org.exoplatform.poll.dao.PollOptionDAO;
import org.exoplatform.poll.entity.PollEntity;
import org.exoplatform.poll.entity.PollOptionEntity;
import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.utils.EntityMapper;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.xml.*", "org.xml.*" })
public class PollStorageTest {
  private PollStorage   pollStorage;

  private PollDAO       pollDAO;

  private PollOptionDAO pollOptionDAO;

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
    poll.setCreatedDate(startDate.toInstant().atZone(ZoneOffset.UTC));
    poll.setEndDate(endDate.toInstant().atZone(ZoneOffset.UTC));
    poll.setCreatorId(1L);

    PollOption pollOption = new PollOption();
    pollOption.setId(1L);
    pollOption.setPollId(poll.getId());
    pollOption.setDescription("pollOption");

    PollEntity pollEntity = new PollEntity();
    pollEntity.setId(1L);
    pollEntity.setQuestion("q1");
    pollEntity.setCreatedDate(startDate);
    pollEntity.setEndDate(endDate);
    pollEntity.setCreatorId(1L);

    PollOptionEntity pollOptionEntity = new PollOptionEntity();
    pollOptionEntity.setPollId(pollEntity.getId());
    pollOptionEntity.setDescription(pollOption.getDescription());
    pollOptionEntity.setId(1L);

    when(pollDAO.create(anyObject())).thenReturn(pollEntity);
    when(pollOptionDAO.create(anyObject())).thenReturn(pollOptionEntity);
    PowerMockito.mockStatic(EntityMapper.class);
    when(EntityMapper.toPollEntity(poll)).thenReturn(pollEntity);
    when(EntityMapper.fromPollEntity(pollEntity)).thenReturn(poll);
    when(EntityMapper.toPollOptionEntity(pollOption, pollEntity.getId())).thenReturn(pollOptionEntity);
    when(EntityMapper.fromPollOptionEntity(pollOptionEntity)).thenReturn(pollOption);

    Poll pollCreated = pollStorage.createPoll(poll, Collections.singletonList(pollOption));
    assertNotNull(pollCreated);
    assertEquals(1L, pollCreated.getId());
    assertEquals(poll, pollCreated);
  }
}
