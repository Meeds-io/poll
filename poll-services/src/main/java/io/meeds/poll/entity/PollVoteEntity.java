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
package io.meeds.poll.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "PollVote")
@Table(name = "POLL_VOTE")
@Data
public class PollVoteEntity implements Serializable {

  private static final long serialVersionUID = -7880849687372574040L;

  @Id
  @SequenceGenerator(name = "SEQ_POLL_VOTE_ID", sequenceName = "SEQ_POLL_VOTE_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_POLL_VOTE_ID")
  @Column(name = "POLL_VOTE_ID", nullable = false)
  private Long              id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "POLL_OPTION_ID", nullable = false)
  private PollOptionEntity  pollOption;

  @Column(name = "VOTE_DATE", nullable = false)
  private Date              voteDate;

  @Column(name = "VOTER_ID", nullable = false)
  private Long              voterId;

}
