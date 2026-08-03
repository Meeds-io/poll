/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2026 Meeds Association contact@meeds.io
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
package io.meeds.poll.listener;

import static io.meeds.poll.utils.PollUtils.POLL_ACTIVITY_TYPE;
import static io.meeds.poll.utils.PollUtils.POLL_ID;
import static io.meeds.poll.utils.PollUtils.POLL_PUBLICATION_DURATION;

import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.ActivityLifeCycleEvent;
import org.exoplatform.social.core.activity.ActivityListenerPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.manager.ActivityManager;

import io.meeds.poll.model.Poll;
import io.meeds.poll.storage.PollStorage;

@Component
public class PollActivityPublicationListener extends ActivityListenerPlugin {

  private static final Log LOG = ExoLogger.getLogger(PollActivityPublicationListener.class);

  @Autowired
  private ActivityManager  activityManager;

  @Autowired
  private PollStorage      pollStorage;

  @PostConstruct
  public void init() {
    activityManager.addActivityEventListener(this);
  }

  @Override
  public void saveActivity(ActivityLifeCycleEvent event) {
    ExoSocialActivity activity = event.getActivity();
    if (activity == null
        || !StringUtils.equals(POLL_ACTIVITY_TYPE, activity.getType())
        || activity.isHidden()) {
      return;
    }
    Map<String, String> templateParams = activity.getTemplateParams();
    if (templateParams == null || StringUtils.isBlank(templateParams.get(POLL_PUBLICATION_DURATION))) {
      // Not a scheduled poll activity being published
      return;
    }
    try {
      long durationMillis = Long.parseLong(templateParams.get(POLL_PUBLICATION_DURATION));
      Poll poll = pollStorage.getPollById(Long.parseLong(templateParams.get(POLL_ID)));
      if (poll != null) {
        // The poll starts when it is effectively published, thus its duration
        // stays the chosen one, for the poll itself as well as for the
        // statistics computing it from its dates
        Date publicationDate = new Date();
        poll.setCreatedDate(publicationDate);
        poll.setEndDate(new Date(publicationDate.getTime() + durationMillis));
        pollStorage.updatePoll(poll);
      }
      // The param is blanked instead of being removed, since the activity
      // update merges the template params and thus doesn't persist a removal
      templateParams.put(POLL_PUBLICATION_DURATION, "");
      activityManager.updateActivity(activity, false);
    } catch (Exception e) {
      LOG.warn("Error computing the dates of the published poll of activity with id {}", activity.getId(), e);
    }
  }

}
