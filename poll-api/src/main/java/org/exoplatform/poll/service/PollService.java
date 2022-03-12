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

import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.model.PollVote;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.activity.model.ActivityFile;

import java.util.List;

public interface PollService {
  /**
   * Creates a new poll
   *
   * @param poll {@link Poll} object to create
   * @param pollOptions {@link Poll} options objects to create
   * @param spaceId {@link Space} id related to the {@link Poll} to be created
   * @param message Message of {@link Poll} activity to be created
   * @param currentIdentity User identity creating the poll
   * @return created {@link Poll} with generated technical identifier
   * @throws IllegalAccessException when user is not authorized to create a poll
   */
  Poll createPoll(Poll poll, List<PollOption> pollOptions, String spaceId, String message, Identity currentIdentity, List<ActivityFile> files) throws IllegalAccessException;

  /**
   * Retrieves a poll identified by its technical identifier.
   * 
   * @param pollId technical identifier of a poll
   * @param currentIdentity User identity getting the poll
   * @return A {@link Poll} object
   * @throws IllegalAccessException when user is not authorized to get a poll
   */
  Poll getPollById(long pollId, Identity currentIdentity) throws IllegalAccessException;
  
  /**
   * Retrieves options of a poll identified by its technical identifier.
   * 
   * @param pollId technical identifier of a poll
   * @param currentIdentity User identity getting the poll
   * @return A {@link Poll} object
   * @throws IllegalAccessException when user is not authorized to get a poll options
   */
  List<PollOption> getPollOptionsById(long pollId, Identity currentIdentity) throws IllegalAccessException;

  /**
   * Add a vote
   *
   * @param pollVote {@link PollVote} object to create
   * @param spaceId {@link Space} id related to the {@link PollVote} to be added
   * @param currentIdentity User identity creating the poll
   * @return created {@link PollVote} with generated technical identifier
   * @throws IllegalAccessException when user is not authorized to add a vote
   */
  PollVote addVote(PollVote pollVote, String spaceId, Identity currentIdentity) throws IllegalAccessException;
}