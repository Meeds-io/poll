package io.meeds.poll.service;

import org.apache.commons.lang3.RandomUtils;
import org.hibernate.ObjectNotFoundException;
import org.junit.After;
import org.junit.Before;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public abstract class BasePollTest {
  protected IdentityManager identityManager;

  protected SpaceService    spaceService;

  protected Space           space;

  protected Identity        user1Identity;

  protected Identity        user2Identity;

  protected Identity        user3Identity;

  protected PortalContainer container;

  protected PollService     pollService;

  @Before
  public void setUp() throws ObjectNotFoundException {
    container = PortalContainer.getInstance();
    initServices();
    begin();
    injectData();
  }

  private void initServices() {
    pollService = container.getComponentInstanceOfType(PollService.class);
    identityManager = container.getComponentInstanceOfType(IdentityManager.class);
    spaceService = container.getComponentInstanceOfType(SpaceService.class);
  }

  @After
  public void tearDown() throws ObjectNotFoundException {
    end();
  }

  protected void injectData() throws ObjectNotFoundException {
    user1Identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "testuser1");
    user2Identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "testuser2");
    user3Identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "testuser3");
    String displayName = "testSpacePoll" + RandomUtils.nextInt();
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
  
  protected void restartTransaction() {
    int i = 0;
    // Close transactions until no encapsulated transaction
    boolean success = true;
    do {
      try {
        end();
        i++;
      } catch (IllegalStateException e) {
        success = false;
      }
    } while (success);

    // Restart transactions with the same number of encapsulations
    for (int j = 0; j < i; j++) {
      begin();
    }
  }

  protected void begin() {
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(container);
  }

  protected void end() {
    RequestLifeCycle.end();
  }
}
