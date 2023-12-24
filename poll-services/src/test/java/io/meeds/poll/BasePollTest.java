package io.meeds.poll;

import java.util.Random;

import org.junit.Before;

import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.poll.dao.PollDAO;
import io.meeds.poll.dao.PollOptionDAO;
import io.meeds.poll.dao.PollVoteDAO;
import io.meeds.poll.service.PollService;
import io.meeds.poll.storage.PollStorage;

@ConfiguredBy({
                @ConfigurationUnit(scope = ContainerScope.ROOT,
                    path = "conf/configuration.xml"),
                @ConfigurationUnit(scope = ContainerScope.PORTAL,
                    path = "conf/portal/configuration.xml"),
                @ConfigurationUnit(scope = ContainerScope.PORTAL,
                    path = "conf/exo.poll.service-local-configuration.xml"),
})
public abstract class BasePollTest extends BaseExoTestCase { // NOSONAR

  private static final Random   RANDOM     = new Random();

  protected static final String TESTUSER_1 = "testuser1";

  protected static final String TESTUSER_2 = "testuser2";

  protected static final String TESTUSER_3 = "testuser3";

  protected IdentityManager     identityManager;

  protected SpaceService        spaceService;

  protected Space               space;

  protected Identity            user1Identity;

  protected Identity            user2Identity;

  protected Identity            user3Identity;

  protected PollService         pollService;

  protected PollStorage         pollStorage;

  protected PollOptionDAO       pollOptionDAO;

  protected PollDAO             pollDAO;

  protected PollVoteDAO         pollVoteDAO;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    initServices();
    begin();
    initUsers();
    injectSpace();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    end();
  }

  private void initServices() {
    identityManager = getContainer().getComponentInstanceOfType(IdentityManager.class);
    spaceService = getContainer().getComponentInstanceOfType(SpaceService.class);
    pollService = getContainer().getComponentInstanceOfType(PollService.class);
    pollStorage = getContainer().getComponentInstanceOfType(PollStorage.class);
    pollOptionDAO = getContainer().getComponentInstanceOfType(PollOptionDAO.class);
    pollDAO = getContainer().getComponentInstanceOfType(PollDAO.class);
    pollVoteDAO = getContainer().getComponentInstanceOfType(PollVoteDAO.class);
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
}
