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
package org.exoplatform.poll.dao;

import junit.framework.TestCase;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.poll.entity.PollEntity;
import org.exoplatform.services.naming.InitialContextInitializer;

import java.util.Date;

public class PollDAOTest extends TestCase {

  private Date            startDate  = new Date(System.currentTimeMillis());

  private Date            endDate    = new Date(System.currentTimeMillis() + 1);

  private String          question   = "q1";

  private Long            creatorId  = 1L;

  private Long            activityId = 1L;

  private PortalContainer container;

  private PollDAO         pollDAO;

  @Override
  protected void setUp() throws Exception {
    RootContainer rootContainer = RootContainer.getInstance();
    rootContainer.getComponentInstanceOfType(InitialContextInitializer.class);

    container = PortalContainer.getInstance();
    pollDAO = container.getComponentInstanceOfType(PollDAO.class);
    ExoContainerContext.setCurrentContainer(container);
    begin();
  }

  public void testCreatePoll() {
    PollEntity pollEntity = new PollEntity();
    pollEntity.setQuestion(question);
    pollEntity.setCreatedDate(startDate);
    pollEntity.setEndDate(endDate);
    pollEntity.setCreatorId(creatorId);
    pollEntity.setActivityId(activityId);
    PollEntity createdPollEntity = pollDAO.create(pollEntity);

    assertNotNull(createdPollEntity);
    assertNotNull(createdPollEntity.getId());
    assertEquals(question, createdPollEntity.getQuestion());
    assertEquals(startDate, createdPollEntity.getCreatedDate());
    assertEquals(endDate, createdPollEntity.getEndDate());
    assertEquals(creatorId, createdPollEntity.getCreatorId());
    assertEquals(activityId, createdPollEntity.getActivityId());
  }

  @Override
  protected void tearDown() throws Exception {
    end();
  }

  private void begin() {
    RequestLifeCycle.begin(container);
  }

  private void end() {
    RequestLifeCycle.end();
  }
}
