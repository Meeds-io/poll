/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2026 Meeds Association contact@meeds.io
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
package io.meeds.poll.mcp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.meeds.mcp.server.plugin.SpaceScopeResolver;
import io.meeds.poll.model.Poll;
import io.meeds.poll.storage.PollStorage;

import lombok.extern.slf4j.Slf4j;

/**
 * Tells which space a poll belongs to: the space it was posted in. Read as
 * the system, without any access check, for the space scope decision only.
 */
@Service("pollSpaceScopeResolver")
@Profile("mcp-server")
@Slf4j
public class PollSpaceScopeResolver implements SpaceScopeResolver {

  /** The name the poll tools' {@code space_scope.resolver} gives. */
  public static final String NAME = "poll";

  /** The storage the poll is read from, bypassing the service's access check. */
  private final PollStorage  pollStorage;

  /**
   * @param pollStorage the poll storage
   */
  public PollSpaceScopeResolver(PollStorage pollStorage) {
    this.pollStorage = pollStorage;
  }

  /**
   * @return {@value #NAME}
   */
  @Override
  public String getName() {
    return NAME;
  }

  /**
   * @param objectId the poll id
   * @return the id of the space the poll was posted in, or null for a poll of
   *         no space, a missing poll, an id that isn't a number, or a lookup
   *         that fails
   */
  @Override
  public Long spaceOf(String objectId) {
    if (!StringUtils.isNumeric(objectId)) {
      return null;
    }
    try {
      Poll poll = pollStorage.getPollById(Long.parseLong(objectId));
      return poll != null && poll.getSpaceId() > 0 ? poll.getSpaceId() : null;
    } catch (RuntimeException e) {
      log.debug("The space of poll '{}' couldn't be read", objectId, e);
      return null;
    }
  }

}
