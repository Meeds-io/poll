/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2026 Meeds Association contact@meeds.io
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

import static io.meeds.mcp.server.util.McpToolUtils.formatDate;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.common.RealtimeListAccess;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.mcp.server.plugin.McpToolPlugin;
import io.meeds.poll.mcp.model.PollOptionResultModel;
import io.meeds.poll.mcp.model.PollResultModel;
import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.service.PollService;
import io.meeds.poll.utils.PollUtils;

/**
 * MCP tools exposing the Poll add-on to the AI agent (EVA). Every method acts
 * as the current user, so space membership and redactor permissions are
 * enforced by {@link PollService}.
 */
@Service
@Profile("mcp-server")
public class PollMcpTool implements McpToolPlugin {

  private static final String ONE_DAY_DURATION    = "1day";

  private static final String THREE_DAYS_DURATION = "3days";

  private static final String ONE_WEEK_DURATION   = "1week";

  private static final String TWO_WEEKS_DURATION  = "2weeks";

  private static final int    DEFAULT_LIST_LIMIT  = 10;

  private static final int    MAX_LIST_LIMIT      = 20;

  private static final int    ACTIVITY_SCAN_LIMIT = 200;

  private final PollService   pollService;

  private final SpaceService  spaceService;

  private final IdentityManager identityManager;

  private final ActivityManager activityManager;

  public PollMcpTool(PollService pollService,
                     SpaceService spaceService,
                     IdentityManager identityManager,
                     ActivityManager activityManager) {
    this.pollService = pollService;
    this.spaceService = spaceService;
    this.identityManager = identityManager;
    this.activityManager = activityManager;
  }

  /**
   * Creates a poll (with its options) and posts it to a space activity stream.
   */
  public PollResultModel createPoll(Long spaceId,
                                    String question,
                                    List<String> options,
                                    String duration,
                                    String message) throws IllegalAccessException {
    if (spaceId == null) {
      throw new IllegalArgumentException("space_id is required. Resolve the target space id first (e.g. with get_my_spaces).");
    }
    if (StringUtils.isBlank(question)) {
      throw new IllegalArgumentException("question is required and cannot be empty.");
    }
    List<String> cleanOptions = options == null ? List.of()
                                                : options.stream().filter(StringUtils::isNotBlank).map(String::trim).toList();
    if (cleanOptions.size() < 2) {
      throw new IllegalArgumentException("A poll needs at least 2 non-empty options.");
    }
    Date endDate = computeEndDate(duration);

    Identity identity = getCurrentUserAclIdentity();
    Space space = spaceService.getSpaceById(String.valueOf(spaceId));
    if (space == null) {
      throw new IllegalArgumentException("No space found with id " + spaceId
          + ". Ask the user which space to post the poll in and resolve its id.");
    }

    Poll poll = new Poll();
    poll.setQuestion(question.trim());
    poll.setCreatedDate(new Date());
    poll.setEndDate(endDate);
    List<PollOption> pollOptions = cleanOptions.stream().map(desc -> {
      PollOption pollOption = new PollOption();
      pollOption.setDescription(desc);
      return pollOption;
    }).toList();
    String activityMessage = StringUtils.isBlank(message) ? question.trim() : message.trim();

    try {
      Poll created = pollService.createPoll(poll, pollOptions, space.getId(), activityMessage, identity, new ArrayList<>());
      return buildResult(created, identity);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("You are not allowed to create a poll in space '" + space.getDisplayName()
          + "'. Only space members who can post (redactors) may create polls. Tell the user they need redactor rights in that space.");
    }
  }

  /**
   * Casts the current user's vote for a poll option and returns the updated
   * results.
   */
  public PollResultModel voteInPoll(Long pollOptionId) throws IllegalAccessException, ObjectNotFoundException {
    if (pollOptionId == null) {
      throw new IllegalArgumentException("poll_option_id is required. Call get_poll_results first to obtain the option ids.");
    }
    Identity identity = getCurrentUserAclIdentity();
    PollOption option;
    try {
      option = pollService.getPollOptionById(pollOptionId, identity);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("You are not allowed to access this poll option. You must be a member of the poll's space to vote.");
    }
    if (option == null) {
      throw new ObjectNotFoundException("No poll option found with id " + pollOptionId
          + ". Call get_poll_results to list the valid option ids.");
    }
    Poll poll = pollService.getPollById(option.getPollId(), identity);
    if (poll == null) {
      throw new ObjectNotFoundException("The poll for option " + pollOptionId + " no longer exists.");
    }
    if (isClosed(poll)) {
      throw new IllegalStateException("This poll is closed (it ended on " + formatDate(poll.getEndDate())
          + "); voting is no longer possible.");
    }
    if (pollService.didVote(identity, poll.getId())) {
      throw new IllegalStateException("You have already voted in this poll. Each user can vote only once."
          + " Call get_poll_results to see the current results.");
    }
    try {
      pollService.vote(String.valueOf(pollOptionId), String.valueOf(poll.getSpaceId()), identity);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("You are not allowed to vote in this poll. You must be a member of the poll's space.");
    }
    return buildResult(poll, identity);
  }

  /**
   * Returns a poll's question, options and current vote tallies.
   */
  public PollResultModel getPollResults(Long pollId) throws IllegalAccessException, ObjectNotFoundException {
    if (pollId == null) {
      throw new IllegalArgumentException("poll_id is required.");
    }
    Identity identity = getCurrentUserAclIdentity();
    Poll poll;
    try {
      poll = pollService.getPollById(pollId, identity);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("You are not allowed to view this poll. You must be a member of the poll's space.");
    }
    if (poll == null) {
      throw new ObjectNotFoundException("No poll found with id " + pollId + ".");
    }
    return buildResult(poll, identity);
  }

  /**
   * Lists the polls posted in a space (most recent first) with their ids and
   * current results, so a poll can be found without knowing its id.
   */
  public List<PollResultModel> listSpacePolls(Long spaceId, Integer limit) {
    if (spaceId == null) {
      throw new IllegalArgumentException("space_id is required. Resolve the target space id first (e.g. with get_my_spaces).");
    }
    Identity identity = getCurrentUserAclIdentity();
    Space space = spaceService.getSpaceById(String.valueOf(spaceId));
    if (space == null) {
      throw new IllegalArgumentException("No space found with id " + spaceId + ".");
    }
    if (!spaceService.canViewSpace(space, identity.getUserId())) {
      throw new IllegalStateException("You are not allowed to view space '" + space.getDisplayName()
          + "'. You must be a member of the space to list its polls.");
    }
    int max = (limit == null || limit <= 0) ? DEFAULT_LIST_LIMIT : Math.min(limit, MAX_LIST_LIMIT);
    org.exoplatform.social.core.identity.model.Identity spaceIdentity =
        identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName());
    RealtimeListAccess<ExoSocialActivity> listAccess = activityManager.getActivitiesOfSpaceWithListAccess(spaceIdentity);
    int scan = Math.min(listAccess.getSize(), ACTIVITY_SCAN_LIMIT);
    List<ExoSocialActivity> activities = scan == 0 ? List.of() : listAccess.loadAsList(0, scan);

    List<PollResultModel> polls = new ArrayList<>();
    for (ExoSocialActivity activity : activities) {
      if (polls.size() >= max) {
        break;
      }
      Map<String, String> params = activity.getTemplateParams();
      if (!PollUtils.POLL_ACTIVITY_TYPE.equals(activity.getType()) || params == null
          || !params.containsKey(PollUtils.POLL_ID)) {
        continue;
      }
      try {
        Poll poll = pollService.getPollById(Long.parseLong(params.get(PollUtils.POLL_ID)), identity);
        if (poll != null) {
          polls.add(buildResult(poll, identity));
        }
      } catch (NumberFormatException | IllegalAccessException e) {
        // skip polls the current user cannot read or with a malformed reference
      }
    }
    return polls;
  }

  private PollResultModel buildResult(Poll poll, Identity identity) throws IllegalAccessException {
    long pollId = poll.getId();
    String spaceId = String.valueOf(poll.getSpaceId());
    List<PollOption> options = pollService.getPollOptionsByPollId(pollId, identity);
    int total = pollService.getPollTotalVotes(pollId, identity);
    boolean didVote = pollService.didVote(identity, pollId);
    List<PollOptionResultModel> optionModels = new ArrayList<>();
    for (PollOption option : options) {
      int votes = pollService.getPollOptionTotalVotes(option.getId(), spaceId, identity);
      boolean votedByMe = pollService.isPollOptionVoted(option.getId(), spaceId, identity);
      int percentage = total > 0 ? Math.round(votes * 100f / total) : 0;
      optionModels.add(new PollOptionResultModel(option.getId(), option.getDescription(), votes, percentage, votedByMe));
    }
    return new PollResultModel(pollId,
                               poll.getQuestion(),
                               poll.getSpaceId(),
                               poll.getActivityId(),
                               resolveUsername(poll.getCreatorId()),
                               formatDate(poll.getEndDate()),
                               isClosed(poll),
                               total,
                               didVote,
                               optionModels);
  }

  private boolean isClosed(Poll poll) {
    return poll.getEndDate() == null || !poll.getEndDate().after(new Date());
  }

  private String resolveUsername(long identityId) {
    try {
      org.exoplatform.social.core.identity.model.Identity identity = identityManager.getIdentity(String.valueOf(identityId));
      return identity == null ? null : identity.getRemoteId();
    } catch (RuntimeException e) {
      return null;
    }
  }

  private Date computeEndDate(String duration) {
    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    ZonedDateTime end;
    switch (StringUtils.trimToEmpty(duration)) {
    case ONE_DAY_DURATION:
      end = now.plusDays(1);
      break;
    case THREE_DAYS_DURATION:
      end = now.plusDays(3);
      break;
    case ONE_WEEK_DURATION:
      end = now.plusDays(7);
      break;
    case TWO_WEEKS_DURATION:
      end = now.plusDays(14);
      break;
    default:
      throw new IllegalArgumentException("Invalid duration '" + duration
          + "'. Allowed values are: 1day, 3days, 1week, 2weeks.");
    }
    return Date.from(end.toInstant());
  }

}
