/*
 * Copyright (C) 2022 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
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
