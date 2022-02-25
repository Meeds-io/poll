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


@Entity(name = "PollVote")
@ExoEntity
@Table(name = "POLL_VOTE")
public class PollVoteEntity implements Serializable {
  private static final long serialVersionUID = -6532033256222584847L;

  @Id
  @SequenceGenerator(name = "SEQ_POLL_VOTE_ID", sequenceName = "SEQ_POLL_VOTE_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_POLL_VOTE_ID")
  @Column(name = "POLL_VOTE_ID")
  private Long              id;

  @Column(name = "POLL_OPTION_ID", nullable = false)
  private Long              pollOptionId;

  @Column(name = "IDENTITY_ID", nullable = false)
  private Long              identityId;

}
