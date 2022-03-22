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
package io.meeds.poll.dao;

import javax.persistence.TypedQuery;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import io.meeds.poll.entity.PollVoteEntity;

public class PollVoteDAO extends GenericDAOJPAImpl<PollVoteEntity, Long> {

  public int countPollOptionTotalVotes(long pollOptionId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("PollVote.countPollOptionTotalVotes", Long.class);
    query.setParameter("pollOptionId", pollOptionId);
    return query.getSingleResult().intValue();
  }

  public int countPollOptionTotalVotesByUser(long pollOptionId, long userId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("PollVote.countPollOptionTotalVotesByUser", Long.class);
    query.setParameter("pollOptionId", pollOptionId);
    query.setParameter("userId", userId);
    return query.getSingleResult().intValue();
  }
}
