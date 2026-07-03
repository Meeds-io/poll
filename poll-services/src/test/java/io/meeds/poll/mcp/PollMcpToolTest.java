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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.common.RealtimeListAccess;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import io.meeds.poll.utils.PollUtils;

import io.meeds.poll.mcp.model.PollResultModel;
import io.meeds.poll.model.Poll;
import io.meeds.poll.model.PollOption;
import io.meeds.poll.service.PollService;

class PollMcpToolTest {

  private static final String    USERNAME  = "testuser1";

  private static final long      POLL_ID   = 7L;

  private static final long      SPACE_ID  = 3L;

  private static final long      OPTION_A  = 10L;

  private static final long      OPTION_B  = 11L;

  private PollService            pollService;

  private SpaceService          spaceService;

  private IdentityManager       identityManager;

  private ActivityManager       activityManager;

  private Identity              currentIdentity;

  private PollMcpTool           pollMcpTool;

  @BeforeEach
  void setUp() {
    pollService = Mockito.mock(PollService.class);
    spaceService = Mockito.mock(SpaceService.class);
    identityManager = Mockito.mock(IdentityManager.class);
    activityManager = Mockito.mock(ActivityManager.class);
    currentIdentity = new Identity(USERNAME);
    pollMcpTool = new PollMcpTool(pollService, spaceService, identityManager, activityManager) {
      @Override
      public Identity getCurrentUserAclIdentity() {
        return currentIdentity;
      }
    };
  }

  private Poll openPoll() {
    Poll poll = new Poll();
    poll.setId(POLL_ID);
    poll.setQuestion("Best day for the offsite?");
    poll.setSpaceId(SPACE_ID);
    poll.setActivityId(99L);
    poll.setCreatorId(42L);
    poll.setCreatedDate(new Date());
    poll.setEndDate(new Date(System.currentTimeMillis() + 86_400_000L)); // +1 day
    return poll;
  }

  private void stubResults(Poll poll, int votesA, int votesB, boolean didVote) throws IllegalAccessException {
    PollOption a = new PollOption();
    a.setId(OPTION_A);
    a.setPollId(poll.getId());
    a.setDescription("Monday");
    PollOption b = new PollOption();
    b.setId(OPTION_B);
    b.setPollId(poll.getId());
    b.setDescription("Friday");
    when(pollService.getPollOptionsByPollId(eq(poll.getId()), any())).thenReturn(List.of(a, b));
    when(pollService.getPollTotalVotes(eq(poll.getId()), any())).thenReturn(votesA + votesB);
    when(pollService.didVote(any(), eq(poll.getId()))).thenReturn(didVote);
    when(pollService.getPollOptionTotalVotes(eq(OPTION_A), any(), any())).thenReturn(votesA);
    when(pollService.getPollOptionTotalVotes(eq(OPTION_B), any(), any())).thenReturn(votesB);
    lenient().when(pollService.isPollOptionVoted(eq(OPTION_A), any(), any())).thenReturn(false);
    lenient().when(pollService.isPollOptionVoted(eq(OPTION_B), any(), any())).thenReturn(didVote);
  }

  // --- create_poll ---------------------------------------------------------

  @Test
  void createPoll() throws Exception {
    Space space = new Space();
    space.setId(String.valueOf(SPACE_ID));
    space.setDisplayName("Engineering");
    when(spaceService.getSpaceById(String.valueOf(SPACE_ID))).thenReturn(space);
    Poll created = openPoll();
    when(pollService.createPoll(any(), any(), eq(space.getId()), any(), any(), any())).thenReturn(created);
    stubResults(created, 0, 0, false);

    PollResultModel result = pollMcpTool.createPoll(SPACE_ID, "Best day for the offsite?",
                                                    List.of("Monday", "Friday"), "1week", null);

    assertNotNull(result);
    assertEquals(POLL_ID, result.getId());
    assertEquals(2, result.getOptions().size());
    assertFalse(result.isClosed());
    verify(pollService).createPoll(any(), any(), eq(space.getId()), any(), any(), any());
  }

  @Test
  void createPollInvalidDurationFails() {
    Space space = new Space();
    space.setId(String.valueOf(SPACE_ID));
    lenient().when(spaceService.getSpaceById(String.valueOf(SPACE_ID))).thenReturn(space);
    assertThrows(IllegalArgumentException.class,
                 () -> pollMcpTool.createPoll(SPACE_ID, "Q", List.of("a", "b"), "1month", null));
  }

  @Test
  void createPollWithLessThanTwoOptionsFails() {
    assertThrows(IllegalArgumentException.class,
                 () -> pollMcpTool.createPoll(SPACE_ID, "Q", List.of("only one"), "1day", null));
  }

  @Test
  void createPollWithUnknownSpaceFails() {
    when(spaceService.getSpaceById(String.valueOf(SPACE_ID))).thenReturn(null);
    assertThrows(IllegalArgumentException.class,
                 () -> pollMcpTool.createPoll(SPACE_ID, "Q", List.of("a", "b"), "1day", null));
  }

  @Test
  void createPollWithoutRedactorRightsFails() throws Exception {
    Space space = new Space();
    space.setId(String.valueOf(SPACE_ID));
    space.setDisplayName("Engineering");
    when(spaceService.getSpaceById(String.valueOf(SPACE_ID))).thenReturn(space);
    when(pollService.createPoll(any(), any(), any(), any(), any(), any()))
        .thenThrow(new IllegalAccessException("no rights"));
    assertThrows(IllegalStateException.class,
                 () -> pollMcpTool.createPoll(SPACE_ID, "Q", List.of("a", "b"), "1day", null));
  }

  // --- vote_in_poll --------------------------------------------------------

  @Test
  void voteInPoll() throws Exception {
    Poll poll = openPoll();
    PollOption option = new PollOption();
    option.setId(OPTION_A);
    option.setPollId(POLL_ID);
    when(pollService.getPollOptionById(eq(OPTION_A), any())).thenReturn(option);
    when(pollService.getPollById(eq(POLL_ID), any())).thenReturn(poll);
    stubResults(poll, 1, 0, false);

    PollResultModel result = pollMcpTool.voteInPoll(OPTION_A);

    assertNotNull(result);
    assertEquals(1, result.getTotalVotes());
    verify(pollService).vote(eq(String.valueOf(OPTION_A)), eq(String.valueOf(SPACE_ID)), any());
  }

  @Test
  void voteInPollUnknownOptionFails() throws Exception {
    when(pollService.getPollOptionById(eq(OPTION_A), any())).thenReturn(null);
    assertThrows(ObjectNotFoundException.class, () -> pollMcpTool.voteInPoll(OPTION_A));
  }

  @Test
  void voteInPollAlreadyVotedFails() throws Exception {
    Poll poll = openPoll();
    PollOption option = new PollOption();
    option.setId(OPTION_A);
    option.setPollId(POLL_ID);
    when(pollService.getPollOptionById(eq(OPTION_A), any())).thenReturn(option);
    when(pollService.getPollById(eq(POLL_ID), any())).thenReturn(poll);
    when(pollService.didVote(any(), eq(POLL_ID))).thenReturn(true);
    assertThrows(IllegalStateException.class, () -> pollMcpTool.voteInPoll(OPTION_A));
  }

  @Test
  void voteInClosedPollFails() throws Exception {
    Poll poll = openPoll();
    poll.setEndDate(new Date(System.currentTimeMillis() - 86_400_000L)); // ended yesterday
    PollOption option = new PollOption();
    option.setId(OPTION_A);
    option.setPollId(POLL_ID);
    when(pollService.getPollOptionById(eq(OPTION_A), any())).thenReturn(option);
    when(pollService.getPollById(eq(POLL_ID), any())).thenReturn(poll);
    assertThrows(IllegalStateException.class, () -> pollMcpTool.voteInPoll(OPTION_A));
  }

  // --- get_poll_results ----------------------------------------------------

  @Test
  void getPollResults() throws Exception {
    Poll poll = openPoll();
    when(pollService.getPollById(eq(POLL_ID), any())).thenReturn(poll);
    stubResults(poll, 3, 1, true);

    PollResultModel result = pollMcpTool.getPollResults(POLL_ID);

    assertNotNull(result);
    assertEquals(4, result.getTotalVotes());
    assertTrue(result.isDidVote());
    assertEquals(75, result.getOptions().get(0).getPercentage());
    assertEquals(25, result.getOptions().get(1).getPercentage());
  }

  @Test
  void getPollResultsNotFoundFails() throws Exception {
    when(pollService.getPollById(eq(POLL_ID), any())).thenReturn(null);
    assertThrows(ObjectNotFoundException.class, () -> pollMcpTool.getPollResults(POLL_ID));
  }

  @Test
  void getPollResultsUnauthorizedFails() throws Exception {
    when(pollService.getPollById(eq(POLL_ID), any())).thenThrow(new IllegalAccessException("nope"));
    assertThrows(IllegalStateException.class, () -> pollMcpTool.getPollResults(POLL_ID));
  }

  @Test
  void voteInPollNullFails() {
    assertThrows(IllegalArgumentException.class, () -> pollMcpTool.voteInPoll(null));
  }

  // --- list_space_polls ----------------------------------------------------

  @Test
  @SuppressWarnings("unchecked")
  void listSpacePolls() throws Exception {
    Space space = new Space();
    space.setId(String.valueOf(SPACE_ID));
    space.setPrettyName("engineering");
    space.setDisplayName("Engineering");
    when(spaceService.getSpaceById(String.valueOf(SPACE_ID))).thenReturn(space);
    when(spaceService.canViewSpace(eq(space), eq(USERNAME))).thenReturn(true);
    when(identityManager.getOrCreateIdentity(any(), any()))
        .thenReturn(new org.exoplatform.social.core.identity.model.Identity("1"));

    ExoSocialActivity pollActivity = new ExoSocialActivityImpl();
    pollActivity.setType(PollUtils.POLL_ACTIVITY_TYPE);
    pollActivity.setTemplateParams(java.util.Map.of(PollUtils.POLL_ID, String.valueOf(POLL_ID)));
    ExoSocialActivity otherActivity = new ExoSocialActivityImpl();
    otherActivity.setType("DEFAULT_ACTIVITY");

    RealtimeListAccess<ExoSocialActivity> listAccess = Mockito.mock(RealtimeListAccess.class);
    when(listAccess.getSize()).thenReturn(2);
    when(listAccess.loadAsList(eq(0), Mockito.anyInt())).thenReturn(List.of(pollActivity, otherActivity));
    when(activityManager.getActivitiesOfSpaceWithListAccess(any())).thenReturn(listAccess);

    Poll poll = openPoll();
    when(pollService.getPollById(eq(POLL_ID), any())).thenReturn(poll);
    stubResults(poll, 2, 1, true);

    List<PollResultModel> polls = pollMcpTool.listSpacePolls(SPACE_ID, null);

    assertNotNull(polls);
    assertEquals(1, polls.size());
    assertEquals(POLL_ID, polls.get(0).getId());
    assertEquals(3, polls.get(0).getTotalVotes());
  }

  @Test
  void listSpacePollsUnknownSpaceFails() {
    when(spaceService.getSpaceById(String.valueOf(SPACE_ID))).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> pollMcpTool.listSpacePolls(SPACE_ID, null));
  }

  @Test
  void listSpacePollsNotMemberFails() {
    Space space = new Space();
    space.setId(String.valueOf(SPACE_ID));
    space.setDisplayName("Engineering");
    when(spaceService.getSpaceById(String.valueOf(SPACE_ID))).thenReturn(space);
    when(spaceService.canViewSpace(eq(space), eq(USERNAME))).thenReturn(false);
    assertThrows(IllegalStateException.class, () -> pollMcpTool.listSpacePolls(SPACE_ID, null));
  }

}
