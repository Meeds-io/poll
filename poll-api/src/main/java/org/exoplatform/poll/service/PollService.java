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

import java.util.List;

public interface PollService {
  /**
   * Creates a new poll
   *
   * @param poll {@link Poll} object to create
   * @param currentIdentity User identity creating poll
   * @return created {@link Poll} with generated technical identifier
   * @throws IllegalAccessException when user is not authorized to create a poll
   */
  Poll createPoll(Poll poll, List<PollOption> pollOptions, String spaceId, org.exoplatform.services.security.Identity currentIdentity) throws IllegalAccessException;

  /**
   * Retrieves a poll identified by its technical identifier.
   * 
   * @param pollId technical identifier of a challenge
   * @param spaceId technical identifier of a space
   * @param username User name accessing poll
   * @return A {@link Poll} object
   * @throws IllegalAccessException
   */
  Poll getPollById(Long pollId, String spaceId, long username) throws IllegalAccessException;
}
