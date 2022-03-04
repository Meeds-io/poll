package org.exoplatform.poll.utils;

import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class PollUtils {

  public static final long getCurrentUserIdentityId(IdentityManager identityManager, String currentUser) {
    Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentUser);
    return identity == null ? 0 : Long.parseLong(identity.getId());
  }

  public static Date toDate(ZonedDateTime datetime) {
    if (datetime == null) {
      return null;
    }
    return Date.from(datetime.toInstant());
  }

  public static ZonedDateTime fromDate(Date date) {
    if (date == null) {
      return null;
    }
    return date.toInstant().atZone(ZoneOffset.UTC);
  }
}
