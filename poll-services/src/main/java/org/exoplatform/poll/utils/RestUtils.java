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
package org.exoplatform.poll.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

public class RestUtils {
  public static final String getCurrentUser() {
    return ConversationState.getCurrent().getIdentity().getUserId();
  }

  public static final long getCurrentUserIdentityId(IdentityManager identityManager) {
    String currentUser = getCurrentUser();
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
