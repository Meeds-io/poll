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
package org.exoplatform.poll.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.rest.model.PollRestEntity;
import org.exoplatform.poll.service.PollService;
import org.exoplatform.poll.utils.PollUtils;
import org.exoplatform.poll.utils.RestEntityBuilder;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.manager.IdentityManager;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/v1/poll")
@Api(value = "/v1/poll", description = "Managing poll") // NOSONAR
public class PollRest implements ResourceContainer {
  private static final Log LOG = ExoLogger.getLogger(PollRest.class);

  private PollService      pollService;

  private IdentityManager  identityManager;

  public PollRest(PollService pollService, IdentityManager identityManager) {
    this.pollService = pollService;
    this.identityManager = identityManager;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @ApiOperation(value = "Create a new poll", httpMethod = "POST", response = Response.class, consumes = "application/json")
  @ApiResponses(value = { @ApiResponse(code = HTTPStatus.NO_CONTENT, message = "Request fulfilled"),
      @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
      @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
      @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), })
  public Response createPoll(@ApiParam(value = "space identifier", required = false) @QueryParam("spaceId") String spaceId,
                             @ApiParam(value = "Poll object to create", required = true) PollRestEntity pollEntity) {
    if (pollEntity == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    org.exoplatform.services.security.Identity currentIdentity = ConversationState.getCurrent().getIdentity();
    long currentUserIdentityId = PollUtils.getCurrentUserIdentityId(identityManager, currentIdentity.getUserId());
    try {
      Poll poll = RestEntityBuilder.toPoll(pollEntity);
      poll.setCreatorId(currentUserIdentityId);
      List<PollOption> pollOptions = RestEntityBuilder.toPollOptions(pollEntity.getOptions());
      poll = pollService.createPoll(poll, pollOptions, spaceId, currentIdentity);
      return Response.ok(poll).build();
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to create a non authorized poll", e);
      return Response.status(Response.Status.UNAUTHORIZED).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
}
