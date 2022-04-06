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
package io.meeds.poll.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.social.core.activity.model.ActivityFile;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.model.PollVote;
import io.meeds.poll.storage.PollStorage;
import io.meeds.poll.utils.PollUtils;

public class PollServiceImpl implements PollService {

  private PollStorage     pollStorage;

  private SpaceService    spaceService;

  private IdentityManager identityManager;

  private ActivityManager activityManager;
  
  private UserACL userACL;


  public PollServiceImpl(PollStorage pollStorage, SpaceService spaceService, IdentityManager identityManager, ActivityManager activityManager, UserACL userACL) {
    this.pollStorage = pollStorage;
    this.spaceService = spaceService;
    this.identityManager = identityManager;
    this.activityManager = activityManager;
    this.userACL = userACL;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Poll createPoll(Poll poll,
                         List<PollOption> pollOptions,
                         String spaceId,
                         String message,
                         org.exoplatform.services.security.Identity currentIdentity,
                         List<ActivityFile> files) throws IllegalAccessException {
    Space space = spaceService.getSpaceById(spaceId);
    if (!spaceService.canRedactOnSpace(space, currentIdentity)) {
      throw new IllegalAccessException("User " + currentIdentity.getUserId() + " is not allowed to create a poll with question " + poll.getQuestion() + " in space " + spaceId);
    }
    long currentUserIdentityId = PollUtils.getCurrentUserIdentityId(identityManager, currentIdentity.getUserId());
    poll.setCreatorId(currentUserIdentityId);
    poll.setSpaceId(Long.parseLong(spaceId));
    Poll createdPoll = pollStorage.createPoll(poll, pollOptions);
    createdPoll =  postPollActivity(message, spaceId, currentIdentity, createdPoll, files);
    PollUtils.broadcastEvent(PollUtils.CREATE_POLL, currentIdentity.getUserId(), createdPoll);//Analytics
    return createdPoll;
  }

  @Override
  public Poll getPollById(long pollId, org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException {
    Poll poll = pollStorage.getPollById(pollId);
    if (poll != null) {
      Space pollSpace = spaceService.getSpaceById(String.valueOf(poll.getSpaceId()));
      if (!canViewPoll(pollSpace, currentIdentity)) {
        throw new IllegalAccessException("User " + currentIdentity.getUserId() + " is not allowed to get a poll with id " + pollId + " in space " + pollSpace.getId());
      }
    }
    return poll;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public PollOption getPollOptionById(long pollOptionId, org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException {
    PollOption pollOption = pollStorage.getPollOptionById(pollOptionId);
    Poll poll = pollStorage.getPollById(pollOption.getPollId());
    if (poll != null) {
      Space pollSpace = spaceService.getSpaceById(String.valueOf(poll.getSpaceId()));
      if (!canViewPoll(pollSpace, currentIdentity)) {
        throw new IllegalAccessException("User " + currentIdentity.getUserId() + " is not allowed to get a poll option with id " + pollOptionId + " in space " + pollSpace.getId());
      }
    }
    return pollOption;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<PollOption> getPollOptionsByPollId(long pollId, org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException {
    Poll poll = pollStorage.getPollById(pollId);
    if (poll != null) {
      Space pollSpace = spaceService.getSpaceById(String.valueOf(poll.getSpaceId()));
      if (!canViewPoll(pollSpace, currentIdentity)) {
        throw new IllegalAccessException("User " + currentIdentity.getUserId() + " is not allowed to get options of poll with id " + pollId + " in space " + pollSpace.getId());
      }
    }
    return pollStorage.getPollOptionsByPollId(pollId);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public PollVote vote(String pollOptionId, String spaceId, org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException {
    Space space = spaceService.getSpaceById(spaceId);
    if (!spaceService.isMember(space, currentIdentity.getUserId())) {
      throw new IllegalAccessException("User " + currentIdentity.getUserId() + " is not allowed to vote a poll option with id " + pollOptionId + " in space " + spaceId);
    }
    PollVote pollVote = new PollVote();
    if (pollStorage.getPollOptionById(Long.parseLong(pollOptionId)) != null) {
      Poll poll = getPollById(pollStorage.getPollOptionById(Long.parseLong(pollOptionId)).getPollId(), currentIdentity);
      if (poll != null) {
        if (!poll.getEndDate().after(new Date())) {
          throw new IllegalAccessException("User " + currentIdentity.getUserId() + " is not allowed to vote in an expired poll with id " + poll.getId() );
        }
        long currentUserIdentityId = PollUtils.getCurrentUserIdentityId(identityManager, currentIdentity.getUserId());
        pollVote.setVoterId(currentUserIdentityId);
        pollVote.setPollOptionId(Long.parseLong(pollOptionId));
        pollVote.setVoteDate(new Date());
        pollVote = pollStorage.createPollVote(pollVote);
        PollUtils.broadcastEvent(PollUtils.VOTE_POLL, currentIdentity.getUserId(), poll);//Analytics
      }
    }
    return pollVote;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPollOptionTotalVotes(long pollOptionId, String spaceId, org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException {
    Space pollSpace = spaceService.getSpaceById(spaceId);
    if (!canViewPoll(pollSpace, currentIdentity)) {
      throw new IllegalAccessException("User " + currentIdentity.getUserId() + " is not allowed to get total votes of poll option with id " + pollOptionId + " in space " + spaceId);
    }
    return pollStorage.countPollOptionTotalVotes(pollOptionId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPollOptionVoted(long pollOptionId, String spaceId, org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException {
    Space pollSpace = spaceService.getSpaceById(spaceId);
    if (!canViewPoll(pollSpace, currentIdentity)) {
      throw new IllegalAccessException("User " + currentIdentity.getUserId() + " is not allowed to check if poll option with id " + pollOptionId + " is voted in space " + pollSpace.getId());
    }
    long currentUserIdentityId = PollUtils.getCurrentUserIdentityId(identityManager, currentIdentity.getUserId());
    return pollStorage.countPollOptionTotalVotesByUser(pollOptionId, currentUserIdentityId) > 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPollOptionsNumber(long pollId, org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException {
    Poll poll = pollStorage.getPollById(pollId);
    if (poll != null) {
      Space pollSpace = spaceService.getSpaceById(String.valueOf(poll.getSpaceId()));
      if (!canViewPoll(pollSpace, currentIdentity)) {
        throw new IllegalAccessException("User " + currentIdentity.getUserId() + " is not allowed to get options number of poll with id " + pollId + " in space " + pollSpace.getId());
      }
    }
    return pollStorage.countPollOptionsByPollId(pollId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPollTotalVotes(long pollId, org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException {
    Poll poll = pollStorage.getPollById(pollId);
    if (poll != null) {
      Space pollSpace = spaceService.getSpaceById(String.valueOf(poll.getSpaceId()));
      if (!canViewPoll(pollSpace, currentIdentity)) {
        throw new IllegalAccessException("User " + currentIdentity.getUserId() + " is not allowed to get total votes of poll with id " + pollId + " in space " + pollSpace.getId());
      }
    }
    return pollStorage.countPollTotalVotes(pollId);
  }
  
  private Poll postPollActivity(String message,
                                String spaceId,
                                org.exoplatform.services.security.Identity currentIdentity,
                                Poll createdPoll,
                                List<ActivityFile> files) {
    Space space = spaceService.getSpaceById(spaceId);
    Identity spaceIdentity = identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName());
    Identity pollActivityCreatorIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentIdentity.getUserId());

    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(message);
    activity.setType(PollUtils.POLL_ACTIVITY_TYPE);
    activity.setUserId(pollActivityCreatorIdentity.getId());
    activity.setFiles(files);
    Map<String, String> templateParams = new HashMap<>();
    templateParams.put(PollUtils.POLL_ID, String.valueOf(createdPoll.getId()));
    activity.setTemplateParams(templateParams);

    activityManager.saveActivityNoReturn(spaceIdentity, activity);
    createdPoll.setActivityId(Long.parseLong(activity.getId()));
    return pollStorage.updatePoll(createdPoll);
  }

  private boolean canViewPoll(Space pollSpace, org.exoplatform.services.security.Identity currentIdentity) {
    return spaceService.isMember(pollSpace, currentIdentity.getUserId()) || currentIdentity.isMemberOf(userACL.getAdminGroups())
        || StringUtils.equals(userACL.getSuperUser(), currentIdentity.getUserId())
        || spaceService.isSuperManager(currentIdentity.getUserId());
  }
}