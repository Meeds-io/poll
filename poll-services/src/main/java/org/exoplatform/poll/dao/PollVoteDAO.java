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
package org.exoplatform.poll.dao;

import javax.persistence.TypedQuery;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.poll.entity.PollVoteEntity;



public class PollVoteDAO extends GenericDAOJPAImpl<PollVoteEntity, Long> {

    public int getTotalVotesByOption(Long pollOptionId) {
        TypedQuery<Long> query = getEntityManager().createNamedQuery("PollVote.getTotalVotesByOption",
                Long.class);
        query.setParameter("pollOptionId", pollOptionId);
        return query.getSingleResult().intValue();
    }

    public Boolean checkVoted(Long pollOptionId, long userId) {
        TypedQuery<Long> query = getEntityManager().createNamedQuery("PollVote.checkVoted",
                Long.class);
        query.setParameter("pollOptionId", pollOptionId);
        query.setParameter("userId", userId);
        return query.getSingleResult().intValue() != 0 ;
    }
}
