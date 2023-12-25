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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.poll;

import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.kernel.test.AbstractSpringTest;

@ConfiguredBy({
  @ConfigurationUnit(scope = ContainerScope.ROOT,
      path = "conf/configuration.xml"),
  @ConfigurationUnit(scope = ContainerScope.PORTAL,
      path = "conf/portal/configuration.xml"),
  @ConfigurationUnit(scope = ContainerScope.PORTAL,
      path = "conf/exo.poll.service-local-configuration.xml"),
})
public abstract class BasePollTest extends AbstractSpringTest { // NOSONAR

  public static final String    MODULE_NAME    = "io.meeds.poll";

  public static final String    CHANGELOG_PATH = "classpath:db/changelog/poll-rdbms.db.changelog.xml";

  protected static final String TESTUSER_1     = "testuser1";

  protected static final String TESTUSER_2     = "testuser2";

  protected static final String TESTUSER_3     = "testuser3";

  private static final Random   RANDOM         = new Random();

  @Autowired
  protected IdentityManager     identityManager;

  @Autowired
  protected SpaceService        spaceService;

  protected Space               space;

  protected Identity            user1Identity;

  protected Identity            user2Identity;

  protected Identity            user3Identity;

  @BeforeEach
  public void beforeEach() {
    begin();
    initUsers();
    injectSpace();
  }

  @AfterEach
  protected void afterEach() {
    end();
  }

  protected void initUsers() {
    user1Identity = identityManager.getOrCreateUserIdentity(TESTUSER_1);
    user2Identity = identityManager.getOrCreateUserIdentity(TESTUSER_2);
    user3Identity = identityManager.getOrCreateUserIdentity(TESTUSER_3);
  }

  protected void injectSpace() {
    String displayName = "testSpacePoll" + RANDOM.nextInt();
    space = spaceService.getSpaceByDisplayName(displayName);
    if (space == null) {
      space = createSpace(displayName, user1Identity.getRemoteId(), user2Identity.getRemoteId());
    }
    if (!spaceService.isMember(space, user1Identity.getRemoteId())) {
      spaceService.addMember(space, user1Identity.getRemoteId());
    }
  }

  protected Space createSpace(String displayName, String... members) {
    Space newSpace = new Space();
    newSpace.setDisplayName(displayName);
    newSpace.setPrettyName(displayName);
    newSpace.setManagers(new String[] { "root" });
    newSpace.setMembers(members);
    newSpace.setRegistration(Space.OPEN);
    newSpace.setVisibility(Space.PRIVATE);
    return spaceService.createSpace(newSpace, "root");
  }

  public void testNop() {
    // Kept for Backward compatibility with JUnit 3
    // Until the full upgrade to JUnit 5 is done
  }

}
