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
package org.exoplatform.poll.rest;

import io.swagger.annotations.*;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.rest.model.PollEntity;
import org.exoplatform.poll.service.PollService;
import org.exoplatform.poll.utils.RestEntityBuilder;
import org.exoplatform.poll.utils.RestUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.manager.IdentityManager;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v1/poll")
@Api(value = "/v1/poll", description = "Managing poll")
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
  @ApiResponses(
          value = {
                  @ApiResponse(code = HTTPStatus.NO_CONTENT, message = "Request fulfilled"),
                  @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
                  @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
                  @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"),
          }
  )
  public Response createPoll(
                            @ApiParam(value = "IANA Time zone identitifer", required = false)
                            @QueryParam("timeZoneId")
                            String timeZoneId,
                            @ApiParam(value = "Poll object to create", required = true)
                                      PollEntity pollEntity) {
    if (pollEntity == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    long userIdentityId = RestUtils.getCurrentUserIdentityId(identityManager);
    try {
      Poll poll = RestEntityBuilder.toPoll(pollEntity, timeZoneId);
      List<PollOption> createdOptions = RestEntityBuilder.toPollOption(pollEntity.getDateOptions());
      poll = pollService.createPoll(poll, createdOptions, userIdentityId);
      return Response.ok(poll).build();
    } catch (IllegalAccessException e) {
      LOG.error("User '{}' attempts to update a non authorized event", e);
      return Response.status(Response.Status.UNAUTHORIZED).build();
    } catch (Exception e) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }
}
