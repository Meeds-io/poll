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
import org.exoplatform.poll.entity.PollOptionEntity;
import org.exoplatform.services.naming.InitialContextInitializer;

import java.util.Date;
import java.util.List;

public class PollOptionDAOTest extends TestCase {

  private Date            startDate  = new Date(1508484583259L);

  private Date            endDate    = new Date(1508484583260L);

  private String          question   = "q1";

  private String          description   = "pollOption description";


  private Long            creatorId  = 1L;

  private Long            activityId = 0L;

  private Long            activityId2 = 1L;


  private Long            spaceId = 1L;

  private PortalContainer container;

  private PollOptionDAO         pollOptionDAO;

  private PollDAO         pollDAO;


  @Override
  protected void setUp() throws Exception {
    RootContainer rootContainer = RootContainer.getInstance();
    rootContainer.getComponentInstanceOfType(InitialContextInitializer.class);

    container = PortalContainer.getInstance();
    pollOptionDAO = container.getComponentInstanceOfType(PollOptionDAO.class);
    pollDAO = container.getComponentInstanceOfType(PollDAO.class);
    ExoContainerContext.setCurrentContainer(container);
    begin();
  }

  public void testFindPollOptionsById() {
    PollEntity pollEntity = new PollEntity();
    pollEntity.setQuestion(question);
    pollEntity.setCreatedDate(startDate);
    pollEntity.setEndDate(endDate);
    pollEntity.setCreatorId(creatorId);
    pollEntity.setActivityId(activityId);
    pollEntity.setSpaceId(spaceId);
    PollEntity createdPollEntity = pollDAO.create(pollEntity);

    assertNotNull(createdPollEntity);
    assertNotNull(createdPollEntity.getId());
    assertEquals(question, createdPollEntity.getQuestion());
    assertEquals(startDate, createdPollEntity.getCreatedDate());
    assertEquals(endDate, createdPollEntity.getEndDate());
    assertEquals(creatorId, createdPollEntity.getCreatorId());
    assertEquals(activityId, createdPollEntity.getActivityId());
    assertEquals(spaceId, createdPollEntity.getSpaceId());

    PollOptionEntity pollOptionEntity = new PollOptionEntity();
    pollOptionEntity.setPollId(createdPollEntity.getId());
    pollOptionEntity.setDescription(description);

    PollOptionEntity createdPollOption = pollOptionDAO.create(pollOptionEntity);

    assertNotNull(createdPollOption);
    assertNotNull(createdPollOption.getId());
    assertEquals(createdPollEntity.getId(), createdPollOption.getPollId());
    assertEquals(description, createdPollOption.getDescription());

    List<PollOptionEntity> pollOptions = pollOptionDAO.findPollOptionsById(createdPollEntity.getId());

    assertNotNull(pollOptions);
    assertEquals(1, pollOptions.size());
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
