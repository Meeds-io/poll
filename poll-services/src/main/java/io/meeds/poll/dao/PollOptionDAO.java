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

import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import io.meeds.poll.entity.PollOptionEntity;
import io.meeds.poll.utils.PollUtils;

@Component
public class PollOptionDAO extends GenericDAOJPAImpl<PollOptionEntity, Long> {

  public List<PollOptionEntity> findPollOptionsByPollId(Long pollId) {
    TypedQuery<PollOptionEntity> query = getEntityManager().createNamedQuery("PollOption.findPollOptionsByPollId", PollOptionEntity.class);
    query.setParameter(PollUtils.POLL_ID, pollId);
    List<PollOptionEntity> resultList = query.getResultList();
    return resultList == null ? Collections.emptyList() : resultList;
  }

  public int countPollOptionsByPollId(long pollId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("PollOption.countPollOptionsByPollId", Long.class);
    query.setParameter("pollId", pollId);
    try {
      return query.getSingleResult().intValue();
    } catch (NoResultException e) {
      throw new NoResultException();
    }
  }
}
