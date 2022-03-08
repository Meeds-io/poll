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
package org.exoplatform.poll.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.storage.PollStorage;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public class PollServiceImpl implements PollService {

  private final static String ACTIVITY_TYPE = "poll";
  private final static String POLL_ID = "pollId";

  private PollStorage     pollStorage;

  private SpaceService    spaceService;

  private IdentityManager identityManager;

  private ActivityManager activityManager;


  public PollServiceImpl(PollStorage pollStorage, SpaceService spaceService, IdentityManager identityManager, ActivityManager activityManager) {
    this.pollStorage = pollStorage;
    this.spaceService = spaceService;
    this.identityManager = identityManager;
    this.activityManager = activityManager;
  }

  @Override
  public Poll createPoll(Poll poll,
                         List<PollOption> pollOptions,
                         String spaceId,
                         String message,
                         org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException {
    Space space = spaceService.getSpaceById(spaceId);
    if (!spaceService.canRedactOnSpace(space, currentIdentity)) {
      throw new IllegalAccessException("User " + currentIdentity + "is not allowed to create a poll with question "
          + poll.getQuestion());
    }
    Poll createdPoll = pollStorage.createPoll(poll, pollOptions);
    String activityId = postPollActivity(message,spaceId, currentIdentity, createdPoll.getId());
    return  createdPoll;

  }

  @Override
  public Poll getPollById(long pollId) throws IllegalStateException {
    Poll poll = pollStorage.getPollById(pollId);
    if (poll == null) {
      throw new IllegalStateException("Poll with id " + pollId + " does not exist");
    }
    return poll;
  }
  
  private String postPollActivity(String message,
                                  String spaceId,
                                  org.exoplatform.services.security.Identity currentIdentity,
                                  long pollId) throws IllegalAccessException {
    Space space = spaceService.getSpaceById(spaceId);
    Identity spaceIdentity = identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName());

    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(message);
    activity.setType(ACTIVITY_TYPE);
    activity.setUserId(currentIdentity.getUserId());
    Map<String, String> templateParams = new HashMap<>();
    templateParams.put(POLL_ID, String.valueOf(pollId));
    activity.setTemplateParams(templateParams);

    activityManager.saveActivityNoReturn(spaceIdentity, activity);
    return activity.getId();
  }
}