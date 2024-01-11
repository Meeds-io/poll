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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.meeds.poll.entity.PollVoteEntity;

public interface PollVoteDAO extends JpaRepository<PollVoteEntity, Long> {

  int countByPollOptionId(long pollOptionId);

  @Query("""
          SELECT COUNT(pv) FROM PollVote pv
          WHERE pv.pollOption.id = ?1
          AND pv.voterId = ?2
      """)
  int countByPollOptionIdAndVoterId(long pollOptionId, long userId);

  @Query("""
          SELECT COUNT(pv) FROM PollVote pv
          INNER JOIN pv.pollOption po ON po.poll.id = ?1
      """)
  int countByPollId(long pollId);

  @Query("""
          SELECT COUNT(pv) FROM PollVote pv
          INNER JOIN pv.pollOption po ON po.poll.id = ?1
          WHERE pv.voterId = ?2
      """)
  long countByPollIdAndVoterId(Long pollId, long userId);

}
