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

import java.util.List;

import javax.persistence.TypedQuery;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.poll.entity.PollOptionEntity;
import java.util.Collections;

public class PollOptionDAO extends GenericDAOJPAImpl<PollOptionEntity, Long> {

  public List<PollOptionEntity> findPollOptionsById(Long pollId) {
    TypedQuery<PollOptionEntity> query = getEntityManager().createNamedQuery("PollOption.findPollOptionsById",
                                                                             PollOptionEntity.class);
    query.setParameter("pollId", pollId);
    List<PollOptionEntity> resultList = query.getResultList();
    return resultList == null ? Collections.emptyList() : resultList;
  }
}
