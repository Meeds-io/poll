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
package io.meeds.poll.utils;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.poll.model.Poll;

public class PollUtils {

  private static final Log LOG = ExoLogger.getLogger(PollUtils.class);

  public static final String POLL_ACTIVITY_TYPE = "poll";
  
  public static final String POLL_ID = "pollId";

  public static final String CREATE_POLL = "meeds.poll.createPoll";

  public static final String VOTE_POLL = "meeds.poll.votePoll";

  private PollUtils() {
  }

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

  public static void broadcastEvent(String eventName, Object source, Object data) {
    try {
      ListenerService listenerService = CommonsUtils.getService(ListenerService.class);
      listenerService.broadcast(eventName, source, data);
    } catch (Exception e) {
      LOG.warn("Error when broadcasting event '" + eventName + "' using source '" + source + "' and data " + data, e);
    }
  }
  
  public static long getPollDuration(Poll poll) {
    long duration = Math.abs(poll.getEndDate().getTime() - poll.getCreatedDate().getTime());
    return TimeUnit.DAYS.convert(duration, TimeUnit.MILLISECONDS);
  }
}
