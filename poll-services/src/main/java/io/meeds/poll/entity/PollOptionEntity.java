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

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "PollOption")
@ExoEntity
@Table(name = "POLL_OPTION")
@NamedQuery(name = "PollOption.findPollOptionsById", query = "SELECT pollOpt FROM PollOption pollOpt where pollOpt.pollId = :pollId")
public class PollOptionEntity implements Serializable {

  private static final long serialVersionUID = 8803249235458041880L;

  @Id
  @SequenceGenerator(name = "SEQ_POLL_OPTION_ID", sequenceName = "SEQ_POLL_OPTION_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_POLL_OPTION_ID")
  @Column(name = "POLL_OPTION_ID")
  private Long              id;

  @Column(name = "POLL_ID", nullable = false)
  private Long              pollId;

  @Column(name = "DESCRIPTION", nullable = false)
  private String            description;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getPollId() {
    return pollId;
  }

  public void setPollId(Long pollId) {
    this.pollId = pollId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
