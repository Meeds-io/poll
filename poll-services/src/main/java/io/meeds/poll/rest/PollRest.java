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

import static io.meeds.poll.utils.RestEntityBuilder.fromPoll;

import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.model.PollVote;
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
public class PollRest extends Application {

  private static final Log LOG = ExoLogger.getLogger(PollRest.class);

  @Autowired
  private PollService      pollService;

  @PostMapping(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
  @Secured("users")
  @Operation(summary = "Create a new poll", method = "POST", description = "Create a new poll")
  @ApiResponses(value = { @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
                          @ApiResponse(responseCode = "50", description = "Internal server error"), })
  public ResponseEntity<PollRestEntity> createPoll(
                                                   @Parameter(description = "space identifier")
                                                   @RequestParam("spaceId")
                                                   String spaceId,
                                                   @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Poll object to create",
                                                                                                         required = true)
                                                   @RequestBody
                                                   PollRestEntity pollRestEntity) {
    if (pollRestEntity == null) {
      return ResponseEntity.badRequest().build();
    }
    Identity currentIdentity = ConversationState.getCurrent().getIdentity();
    try {
      Poll poll = RestEntityBuilder.toPoll(pollRestEntity);
      List<PollOption> pollOptions = RestEntityBuilder.toPollOptions(pollRestEntity.getOptions());
      poll = pollService.createPoll(poll,
                                    pollOptions,
                                    spaceId,
                                    pollRestEntity.getMessage(),
                                    currentIdentity,
                                    pollRestEntity.getFiles());
      return ResponseEntity.ok(fromPoll(pollService, poll, currentIdentity));
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to create a non authorized poll", currentIdentity.getUserId(), e);
      return ResponseEntity.status(HTTPStatus.UNAUTHORIZED).build();
    }
  }

  @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON)
  @Secured("users")
  @Operation(summary = "Get a poll", method = "GET",
             description = "This gets the poll with the given id if the authenticated user is a member of the space.")
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
        return ResponseEntity.notFound().build();
      } else {
        PollRestEntity pollRestEntity = fromPoll(pollService, poll, currentIdentity);
        return ResponseEntity.ok(pollRestEntity);
      }
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to get a non authorized poll with id {}", currentIdentity.getUserId(), pollId, e);
      return ResponseEntity.status(HTTPStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping(path = "/vote/{optionId}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
  @Secured("users")
  @Operation(summary = "Vote in a poll", method = "POST", description = "Vote in a poll")
  @ApiResponses(value = { @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "404", description = "Poll option not found"),
                          @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public ResponseEntity<PollVote> vote(
                                       @Parameter(description = "Poll option id", required = true)
                                       @PathVariable("optionId")
                                       String optionId) {
    Identity currentIdentity = ConversationState.getCurrent().getIdentity();
    try {
      PollOption pollOption = pollService.getPollOptionById(Long.parseLong(optionId), currentIdentity);
      if (pollOption == null) {
        return ResponseEntity.notFound().build();
      }
      Poll poll = pollService.getPollById(pollOption.getPollId(), currentIdentity);
      if (poll == null) {
        return ResponseEntity.notFound().build();
      } else if (pollService.didVote(currentIdentity, pollOption.getPollId())) {
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
                                                                  "User did already vote an option in this poll"))
                             .build();
      } else {
        PollVote pollVote = pollService.vote(optionId, String.valueOf(poll.getSpaceId()), currentIdentity);
        return ResponseEntity.ok(pollVote);
      }
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to vote in a non authorized poll option with id {}", currentIdentity.getUserId(), optionId, e);
      return ResponseEntity.status(HTTPStatus.UNAUTHORIZED).build();
    }
  }

}
