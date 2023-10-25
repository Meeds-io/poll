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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.model.PollVote;
import io.meeds.poll.rest.model.PollOptionRestEntity;
import io.meeds.poll.rest.model.PollRestEntity;
import io.meeds.poll.service.PollService;
import io.meeds.poll.utils.RestEntityBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/polls")
@Tag(name = "/polls", description = "Managing poll")
public class PollRest {

  private static final Log LOG = ExoLogger.getLogger(PollRest.class);

  @Autowired
  private PollService      pollService;

  @PostMapping(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Create a new poll", method = "POST", description = "Create a new poll")
  @ApiResponses(value = { @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
                          @ApiResponse(responseCode = "50", description = "Internal server error"), })
  public Response createPoll(
                             @Parameter(description = "space identifier")
                             @RequestParam("spaceId")
                             String spaceId,
                             @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Poll object to create", required = true)
                             @RequestBody
                             PollRestEntity pollRestEntity) {
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

  @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Get a poll", method = "GET", description = "This gets the poll with the given id if the authenticated user is a member of the space.")
  @ApiResponses(value = { @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "404", description = "Poll not found"),
                          @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public ResponseEntity<PollRestEntity> getPollById(
                                                    @Parameter(description = "Poll id", required = true)
                                                    @PathVariable("id")
                                                    String pollId) {
    Identity currentIdentity = ConversationState.getCurrent().getIdentity();
    try {
      if (StringUtils.isBlank(pollId)) {
        return ResponseEntity.badRequest().build();
      }
      Poll poll = pollService.getPollById(Long.parseLong(pollId), currentIdentity);
      if (poll == null) {
        return ResponseEntity.badRequest().build();
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
      return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(pollRestEntity);
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to get a non authorized poll with id {}", currentIdentity.getUserId(), pollId, e);
      return ResponseEntity.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
    } catch (Exception e) {
      LOG.error("Error when getting a poll by id {} ", pollId, e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @PostMapping(path="/vote/{optionId}", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Vote in a poll", method = "POST", description = "Vote in a poll")
  @ApiResponses(value = { @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "404", description = "Poll option not found"),
                          @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response vote(
                       @Parameter(description = "Poll option id", required = true)
                       @PathVariable("optionId")
                       String optionId) {
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
