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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.rest.model.PollOptionRestEntity;
import io.meeds.poll.rest.model.PollRestEntity;
import io.meeds.poll.service.PollService;

public class RestEntityBuilder {

  private RestEntityBuilder() {
  }

  public static PollRestEntity fromPoll(PollService pollService,
                                        Poll poll,
                                        Identity currentUserIdentity) throws IllegalAccessException {
    List<PollOption> pollOptions = pollService.getPollOptionsByPollId(poll.getId(), currentUserIdentity);
    List<PollOptionRestEntity> pollOptionRestEntities = new ArrayList<>();
    for (PollOption pollOption : pollOptions) {
      int pollOptionVotes = pollService.getPollOptionTotalVotes(pollOption.getId(),
                                                                String.valueOf(poll.getSpaceId()),
                                                                currentUserIdentity);
      boolean isPollOptionVoted = pollService.isPollOptionVoted(pollOption.getId(),
                                                                String.valueOf(poll.getSpaceId()),
                                                                currentUserIdentity);
      PollOptionRestEntity pollOptionRestEntity = fromPollOption(pollOption,
                                                                 pollOptionVotes,
                                                                 isPollOptionVoted);
      pollOptionRestEntities.add(pollOptionRestEntity);
    }
    return fromPoll(poll, pollOptionRestEntities);
  }

  public static final PollRestEntity fromPoll(Poll poll,
                                              List<PollOptionRestEntity> pollOptionRestEntities) {
    PollRestEntity pollRestEntity = new PollRestEntity();
    pollRestEntity.setId(poll.getId());
    pollRestEntity.setQuestion(poll.getQuestion());
    pollRestEntity.setOptions(pollOptionRestEntities);
    pollRestEntity.setEndDateTime(poll.getEndDate().getTime());
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    if (identityManager.getIdentity(String.valueOf(poll.getCreatorId())) != null) {
      pollRestEntity.setCreator(identityManager.getIdentity(String.valueOf(poll.getCreatorId())).getRemoteId());
    }
    return pollRestEntity;
  }

  public static final PollOptionRestEntity fromPollOption(PollOption pollOption, int votes, boolean voted) {
    PollOptionRestEntity pollOptionRestEntity = new PollOptionRestEntity();
    pollOptionRestEntity.setId(pollOption.getId());
    pollOptionRestEntity.setDescription(pollOption.getDescription());
    pollOptionRestEntity.setVotes(votes);
    pollOptionRestEntity.setVoted(voted);
    return pollOptionRestEntity;
  }

  public static final Poll toPoll(PollRestEntity pollRestEntity) {
    Date createdDate = new Date();
    Poll poll = new Poll();
    poll.setQuestion(pollRestEntity.getQuestion());
    poll.setCreatedDate(createdDate);
    try {
      poll.setEndDate(PollUtils.computeEndDate(createdDate, pollRestEntity.getDuration()));
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException(e.getMessage());
    }
    return poll;
  }

  public static final List<PollOption> toPollOptions(List<PollOptionRestEntity> pollOptionEntities) {
    return pollOptionEntities.stream().map(pollOptionEntity -> {
      PollOption pollOption = new PollOption();
      pollOption.setDescription(pollOptionEntity.getDescription());
      return pollOption;
    }).toList();
  }
}
