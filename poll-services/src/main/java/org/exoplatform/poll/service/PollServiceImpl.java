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
import org.exoplatform.poll.utils.PollUtils;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public class PollServiceImpl implements PollService {

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
      throw new IllegalAccessException("User " + currentIdentity.getUserId() + "is not allowed to create a poll with question "
          + poll.getQuestion());
    }
    long currentUserIdentityId = PollUtils.getCurrentUserIdentityId(identityManager, currentIdentity.getUserId());
    poll.setCreatorId(currentUserIdentityId);
    poll.setSpaceId(Long.parseLong(spaceId));
    Poll createdPoll = pollStorage.createPoll(poll, pollOptions);
    String activityId = postPollActivity(message, spaceId, currentIdentity, createdPoll.getId());
    createdPoll.setActivityId(Long.parseLong(activityId));
    return pollStorage.updatePoll(createdPoll);

  }

  @Override
  public Poll getPollById(long pollId, org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException {
    Poll poll = pollStorage.getPollById(pollId);
    Space pollSpace = spaceService.getSpaceById(String.valueOf(poll.getSpaceId()));
    if (!spaceService.isMember(pollSpace, currentIdentity.getUserId())) {
      throw new IllegalAccessException("User " + currentIdentity.getUserId() + "is not allowed to get a poll with id "
          + pollId);
    }
    return poll;
  }
  
  @Override
  public List<PollOption> getPollOptionsById(long pollId, org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException {
    Poll poll = pollStorage.getPollById(pollId);
    Space pollSpace = spaceService.getSpaceById(String.valueOf(poll.getSpaceId()));
    if (!spaceService.isMember(pollSpace, currentIdentity.getUserId())) {
      throw new IllegalAccessException("User " + currentIdentity.getUserId() + "is not allowed to get options of poll with id "
          + pollId);
    }
    return pollStorage.getPollOptionsById(pollId);
  }
  
  private String postPollActivity(String message,
                                  String spaceId,
                                  org.exoplatform.services.security.Identity currentIdentity,
                                  long pollId) throws IllegalAccessException {
    Space space = spaceService.getSpaceById(spaceId);
    Identity spaceIdentity = identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName());
    Identity pollActvityCreatorIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentIdentity.getUserId());

    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(message);
    activity.setType(PollUtils.POLL_ACTIVITY_TYPE);
    activity.setUserId(pollActvityCreatorIdentity.getId());
    Map<String, String> templateParams = new HashMap<>();
    templateParams.put(PollUtils.POLL_ID, String.valueOf(pollId));
    activity.setTemplateParams(templateParams);

    activityManager.saveActivityNoReturn(spaceIdentity, activity);
    return activity.getId();
  }
}