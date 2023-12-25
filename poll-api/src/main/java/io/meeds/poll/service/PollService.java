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

import org.springframework.stereotype.Service;

import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.activity.model.ActivityFile;

import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.model.PollVote;
import org.exoplatform.social.core.space.model.Space;

import java.util.List;

@Service
public interface PollService {
  /**
   * Creates a new poll
   *
   * @param poll {@link Poll} object to create
   * @param pollOptions {@link Poll} options objects to create
   * @param spaceId {@link Space} id related to the {@link Poll} to be created
   * @param message Message of {@link Poll} activity to be created
   * @param currentIdentity User identity creating the {@link Poll}
   * @return created {@link Poll} with generated technical identifier
   * @throws IllegalAccessException when the current user is not authorized to create a {@link Poll}
   */
  Poll createPoll(Poll poll, List<PollOption> pollOptions, String spaceId, String message, Identity currentIdentity, List<ActivityFile> files) throws IllegalAccessException;

  /**
   * Retrieves a poll identified by its technical identifier.
   * 
   * @param pollId technical identifier of a {@link Poll}
   * @param currentIdentity User identity getting the {@link Poll}
   * @return A {@link Poll} object
   * @throws IllegalAccessException when the current user is not authorized to get a {@link Poll}
   */
  Poll getPollById(long pollId, Identity currentIdentity) throws IllegalAccessException;
  
  /**
   * Retrieves a poll option identified by its technical identifier.
   * 
   * @param pollOptionId technical identifier of a {@link Poll}
   * @param currentIdentity User identity getting the {@link Poll} option
   * @return A {@link Poll} option object
   * @throws IllegalAccessException when the current user is not authorized to get a {@link Poll} option
   */
  PollOption getPollOptionById(long pollOptionId, Identity currentIdentity) throws IllegalAccessException;
  
  /**
   * Retrieves options of a poll identified by its technical identifier.
   * 
   * @param pollId technical identifier of a {@link Poll}
   * @param currentIdentity User identity getting the {@link Poll} options
   * @return A {@link Poll} object
   * @throws IllegalAccessException when the current user is not authorized to get poll options of a {@link Poll}
   */
  List<PollOption> getPollOptionsByPollId(long pollId, Identity currentIdentity) throws IllegalAccessException;

  /**
   * Votes a poll option
   *
   * @param pollOptionId {@link Poll} option id to be voted
   * @param spaceId {@link Space} id related to the voted {@link Poll}
   * @param currentIdentity User identity voting in the {@link Poll}
   * @return created {@link Poll} vote with generated technical identifier
   * @throws IllegalAccessException when the current user is not authorized to vote
   */
  PollVote vote(String pollOptionId, String spaceId, Identity currentIdentity) throws IllegalAccessException;

  /**
   * Retrieves total votes of a poll option identified by its technical identifier
   *
   * @param pollOptionId {@link Poll} option technical identifier
   * @param spaceId {@link Space} id related to the {@link Poll} option
   * @param currentIdentity User identity
   * @return The {@link Poll} option total votes
   * @throws IllegalAccessException when the current user is not authorized to get the {@link Poll} option total votes
   */
  int getPollOptionTotalVotes(long pollOptionId, String spaceId, Identity currentIdentity) throws IllegalAccessException;

  /**
   * Checks if a the current user has voted a poll option identified by its technical identifier.
   *
   * @param pollOptionId {@link Poll} option technical identifier to be checked if it is voted
   * @param spaceId {@link Space} id related to the {@link Poll} option
   * @param currentIdentity User identity to be checked if he has voted the {@link Poll} option
   * @return A {@link boolean} which indicates if the current identity has voted the {@link Poll} option
   * @throws IllegalAccessException when the current user is not authorized to check if the {@link Poll} option is voted
   */
  boolean isPollOptionVoted(long pollOptionId, String spaceId, Identity currentIdentity) throws IllegalAccessException;

  /**
   * Retrieves the number of poll options identified by its technical identifier
   *
   * @param pollId technical identifier of a {@link Poll}
   * @param currentIdentity User identifier getting the {@link Poll} options number
   * @return The {@link Poll} options number
   * @throws IllegalAccessException when the current user is not authorized to get {@link Poll} options number
   */
  int getPollOptionsNumber(long pollId, Identity currentIdentity) throws IllegalAccessException;

  /**
   * Retrieves total votes of a poll identified by its technical identifier
   *
   * @param pollId technical identifier of a {@link Poll}
   * @param currentIdentity User identifier getting the {@link Poll} total votes
   * @return The {@link Poll} total votes
   * @throws IllegalAccessException when the current user is not authorized to get {@link Poll} total votes
   */
  int getPollTotalVotes(long pollId, Identity currentIdentity) throws IllegalAccessException;

  /**
   * Checks if the user voted in the poll
   * @param currentIdentity User identifier representing The Voter
   * @param pollId technical identifier of the poll
   * @return boolean true if the user did already vote this Poll
   */
  boolean didVote(Identity currentIdentity, Long pollId);
}
