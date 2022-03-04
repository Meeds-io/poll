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

import java.util.List;

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.storage.PollStorage;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public class PollServiceImpl implements PollService {

  private PollStorage     pollStorage;

  private SpaceService    spaceService;

  private IdentityManager identityManager;

  public PollServiceImpl(PollStorage pollStorage, SpaceService spaceService, IdentityManager identityManager) {
    this.pollStorage = pollStorage;
    this.spaceService = spaceService;
    this.identityManager = identityManager;
  }

  @Override
  public Poll createPoll(Poll poll,
                         List<PollOption> pollOptions,
                         String spaceId,
                         org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException {
    Space space = spaceService.getSpaceById(spaceId);
    if (!spaceService.canRedactOnSpace(space, currentIdentity)) {
      throw new IllegalAccessException("User " + currentIdentity + "is not allowed to create a poll with question "
          + poll.getQuestion());
    }
    return pollStorage.createPoll(poll, pollOptions);
  }

  @Override
  public Poll getPollById(Long pollId, String spaceId, long userIdentityId) throws IllegalAccessException {
    Space space = spaceService.getSpaceById(spaceId);
    if (!canViewPoll(space, userIdentityId)) {
      throw new IllegalAccessException("User " + userIdentityId + "is not allowed to access a poll with id " + pollId);
    }
    Poll poll = pollStorage.getPollById(pollId);
    if (poll == null) {
      return null;
    }
    return poll;
  }
  
  private boolean canViewPoll(Space space, long currentIdentity) {
    Identity identity = identityManager.getIdentity(String.valueOf(currentIdentity));
    if (identity == null) {
      return false;
    }
    return space != null && spaceService.isMember(space, identity.getRemoteId());
  }
  
}
