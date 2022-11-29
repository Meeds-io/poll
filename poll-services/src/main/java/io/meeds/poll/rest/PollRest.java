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
package io.meeds.poll.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.model.PollVote;
import io.meeds.poll.rest.model.PollOptionRestEntity;
import io.meeds.poll.rest.model.PollRestEntity;
import io.meeds.poll.service.PollService;
import io.meeds.poll.utils.RestEntityBuilder;
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

  public PollRest(PollService pollService) {
    this.pollService = pollService;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @ApiOperation(value = "Create a new poll", httpMethod = "POST", response = Response.class, consumes = "application/json")
  @ApiResponses(value = { @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
                          @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), })
  public Response createPoll(@ApiParam(value = "space identifier", required = false) @QueryParam("spaceId") String spaceId,
                             @ApiParam(value = "Poll object to create", required = true) PollRestEntity pollRestEntity) {
    if (pollRestEntity == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    Identity currentIdentity = ConversationState.getCurrent().getIdentity();
    try {
      Poll poll = RestEntityBuilder.toPoll(pollRestEntity);
      List<PollOption> pollOptions = RestEntityBuilder.toPollOptions(pollRestEntity.getOptions());
      poll = pollService.createPoll(poll, pollOptions, spaceId, pollRestEntity.getMessage(), currentIdentity, pollRestEntity.getFiles());
      return Response.ok(poll).build();
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to create a non authorized poll", currentIdentity.getUserId(), e);
      return Response.status(Response.Status.UNAUTHORIZED).build();
    } catch (Exception e) {
      LOG.error("Error when creating a poll ", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  @Path("{id}")
  @RolesAllowed("users")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get a poll", httpMethod = "GET", response = Response.class, notes = "This gets the poll with the given id if the authenticated user is a member of the space.")
  @ApiResponses(value = { @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
                          @ApiResponse(code = HTTPStatus.NOT_FOUND, message = "Poll not found"),
                          @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
                          @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), })
  public Response getPollById(@ApiParam(value = "Poll id", required = true) @PathParam("id") String pollId) {
    Identity currentIdentity = ConversationState.getCurrent().getIdentity();
    try {
      if (StringUtils.isBlank(pollId)) {
        return Response.status(Response.Status.BAD_REQUEST).build();
      }
      Poll poll = pollService.getPollById(Long.parseLong(pollId), currentIdentity);
      if (poll == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      List<PollOption> pollOptions = pollService.getPollOptionsByPollId(Long.parseLong(pollId), currentIdentity);
      List<PollOptionRestEntity> pollOptionRestEntities = new ArrayList<>();
      for (PollOption pollOption : pollOptions) {
        int pollOptionVotes = pollService.getPollOptionTotalVotes(pollOption.getId(), String.valueOf(poll.getSpaceId()), currentIdentity);
        boolean isPollOptionVoted = pollService.isPollOptionVoted(pollOption.getId(), String.valueOf(poll.getSpaceId()), currentIdentity);
        PollOptionRestEntity pollOptionRestEntity = RestEntityBuilder.fromPollOption(pollOption, pollOptionVotes, isPollOptionVoted);
        pollOptionRestEntities.add(pollOptionRestEntity);
      }
      PollRestEntity pollRestEntity = RestEntityBuilder.fromPoll(poll, pollOptionRestEntities);
      return Response.ok(pollRestEntity).build();
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to get a non authorized poll with id {}", currentIdentity.getUserId(), pollId, e);
      return Response.status(Response.Status.UNAUTHORIZED).build();
    } catch (Exception e) {
      LOG.error("Error when getting a poll by id {} ", pollId, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("/vote/{optionId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @ApiOperation(value = "Vote in a poll", httpMethod = "POST", response = Response.class)
  @ApiResponses(value = { @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
                          @ApiResponse(code = HTTPStatus.NOT_FOUND, message = "Poll option not found"),
                          @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
                          @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), })
  public Response vote(@ApiParam(value = "Poll option id", required = true) @PathParam("optionId") String optionId) {
    Identity currentIdentity = ConversationState.getCurrent().getIdentity();
    try {
      PollOption pollOption = pollService.getPollOptionById(Long.parseLong(optionId), currentIdentity);
      if (pollOption == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      Poll poll = pollService.getPollById(pollOption.getPollId(), currentIdentity);
      if (poll == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      if(pollService.didVote(currentIdentity, pollOption.getPollId())) {
        return Response.status(Response.Status.FORBIDDEN).entity("User did already vote an option in this poll").build();
      }
      PollVote pollVote = pollService.vote(optionId, String.valueOf(poll.getSpaceId()), currentIdentity);
      return Response.ok(pollVote).build();
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to vote in a non authorized poll option with id {}", currentIdentity.getUserId(), optionId, e);
      return Response.status(Response.Status.UNAUTHORIZED).build();
    } catch (Exception e) {
      LOG.error("Error when voting in the poll option id {}", optionId, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
}
