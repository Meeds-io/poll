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
package org.exoplatform.poll.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "PollVote")
@ExoEntity
@Table(name = "POLL_VOTE")
@NamedQueries({
        @NamedQuery(name = "PollVote.getTotalVotesByOption", query = "SELECT COUNT(*) FROM PollVote pollVote where pollVote.pollOptionId = :pollOptionId"),
        @NamedQuery(name = "PollVote.checkVoted", query = "SELECT COUNT(*) FROM PollVote pollVote where pollVote.pollOptionId = :pollOptionId " +
                "AND pollVote.voterId = :userId")
})

public class PollVoteEntity implements Serializable {

    private static final long serialVersionUID = -7880849687372574040L;

    @Id
    @SequenceGenerator(name = "SEQ_POLL_VOTE_ID", sequenceName = "SEQ_POLL_VOTE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_POLL_VOTE_ID")
    @Column(name = "POLL_VOTE_ID", nullable = false)
    private Long              id;

    @Column(name = "POLL_OPTION_ID", nullable = false)
    private Long              pollOptionId;

    @Column(name = "VOTER_ID", nullable = false)
    private Long              voterId;

    @Column(name = "VOTE_DATE", nullable = false)
    private Date              voteDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPollOptionId() {
        return pollOptionId;
    }

    public void setPollOptionId(Long pollOptionId) {
        this.pollOptionId = pollOptionId;
    }

    public Long getVoterId() {
        return voterId;
    }

    public void setVoterId(Long voterId) {
        this.voterId = voterId;
    }

    public Date getVoteDate() {
        return voteDate;
    }

    public void setVoteDate(Date voteDate) {
        this.voteDate = voteDate;
    }
}
