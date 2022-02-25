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
package org.exoplatform.poll.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "PollOption")
@ExoEntity
@Table(name = "POLL_OPTION")
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
