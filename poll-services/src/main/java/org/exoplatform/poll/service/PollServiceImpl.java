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

public class PollServiceImpl implements PollService {

  private PollStorage pollStorage;

  public PollServiceImpl(PollStorage pollStorage) {
    this.pollStorage = pollStorage;
  }

  @Override
  public Poll createPoll(Poll poll, List<PollOption> pollOptions, long userIdentityId) throws IllegalAccessException {
    //TODO to verify if needed
    /*if (userIdentityId <= 0) {
      throw new IllegalArgumentException("userIdentityId is mandatory");
    }
    if (poll == null) {
      throw new IllegalArgumentException("Poll is mandatory");
    }*/
    
    //TODO check authorization -> IllegalAccessException
    Poll createdPoll = pollStorage.createPoll(poll, pollOptions);
    //TODO create poll activity
    return createdPoll;
  }
}
