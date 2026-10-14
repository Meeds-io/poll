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
package io.meeds.poll.listener;

import static io.meeds.poll.utils.PollUtils.POLL_ACTIVITY_TYPE;
import static io.meeds.poll.utils.PollUtils.POLL_ID;
import static io.meeds.poll.utils.PollUtils.POLL_PUBLICATION_DURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.exoplatform.social.core.activity.ActivityLifeCycleEvent;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.manager.ActivityManager;

import io.meeds.poll.model.Poll;
import io.meeds.poll.storage.PollStorage;

/**
 * Pins that a poll activity only acts on the poll it is the activity of.
 */
@ExtendWith(MockitoExtension.class)
class PollActivityPublicationListenerTest {

  /** The id of the poll the activities reference. */
  private static final long               POLL_ID_VALUE = 7L;

  /** The id of the activity the poll was posted with. */
  private static final long               ACTIVITY_ID   = 99L;

  /** The id of the space the poll belongs to; it doesn't decide the outcome. */
  private static final long               SPACE_ID      = 3L;

  /** The chosen duration of the poll, in milliseconds. */
  private static final long               DURATION      = 3_600_000L;

  /** The activity manager the listener updates the activity with. */
  @Mock
  private ActivityManager                 activityManager;

  /** The storage the poll is read from and updated in. */
  @Mock
  private PollStorage                     pollStorage;

  /** The creation event handed to the listener. */
  @Mock
  private ActivityLifeCycleEvent          event;

  /** The tested listener. */
  @InjectMocks
  private PollActivityPublicationListener listener;

  /**
   * The activity the poll was posted with sets the dates of that poll from
   * its publication. Mutant: the activity id comparison inverted.
   */
  @Test
  void activityOfThePollUpdatesItsDates() {
    Poll poll = poll(SPACE_ID);
    when(pollStorage.getPollById(POLL_ID_VALUE)).thenReturn(poll);
    ExoSocialActivity activity = activity(String.valueOf(SPACE_ID));
    when(event.getActivity()).thenReturn(activity);
    long before = System.currentTimeMillis();

    listener.saveActivity(event);

    verify(pollStorage).updatePoll(poll);
    assertTrue(poll.getCreatedDate().getTime() >= before);
    assertEquals(DURATION, poll.getEndDate().getTime() - poll.getCreatedDate().getTime());
    verify(activityManager).updateActivity(activity, false);
  }

  /**
   * A forged activity, here posted in another space, referencing a poll by
   * its id leaves that poll untouched: its activity id isn't the poll's.
   * Mutant: the activity id comparison removed.
   */
  @Test
  void forgedActivityOfAnotherSpaceLeavesThePollUntouched() {
    Poll poll = poll(SPACE_ID);
    Date createdDate = poll.getCreatedDate();
    Date endDate = poll.getEndDate();
    when(pollStorage.getPollById(POLL_ID_VALUE)).thenReturn(poll);
    ExoSocialActivity forged = activity(String.valueOf(SPACE_ID + 1));
    forged.setId(String.valueOf(ACTIVITY_ID + 1));
    when(event.getActivity()).thenReturn(forged);

    listener.saveActivity(event);

    verify(pollStorage, never()).updatePoll(any());
    assertEquals(createdDate, poll.getCreatedDate());
    assertEquals(endDate, poll.getEndDate());
    verify(activityManager, never()).updateActivity(any(), anyBoolean());
  }

  /**
   * A forged activity of the very space of the poll, which is not the activity
   * of the poll, leaves the poll untouched. Mutants: the activity id comparison
   * removed, inverted, or replaced by the space comparison alone.
   */
  @Test
  void otherActivityOfTheSpaceOfThePollLeavesThePollUntouched() {
    Poll poll = poll(SPACE_ID);
    Date createdDate = poll.getCreatedDate();
    Date endDate = poll.getEndDate();
    when(pollStorage.getPollById(POLL_ID_VALUE)).thenReturn(poll);
    ExoSocialActivity forged = activity(String.valueOf(SPACE_ID));
    forged.setId(String.valueOf(ACTIVITY_ID + 1));
    when(event.getActivity()).thenReturn(forged);

    listener.saveActivity(event);

    verify(pollStorage, never()).updatePoll(any());
    assertEquals(createdDate, poll.getCreatedDate());
    assertEquals(endDate, poll.getEndDate());
    verify(activityManager, never()).updateActivity(any(), anyBoolean());
  }

  /**
   * An activity with a blank or non numeric id leaves the poll untouched.
   */
  @Test
  void activityWithoutNumericIdLeavesThePollUntouched() {
    when(pollStorage.getPollById(POLL_ID_VALUE)).thenReturn(poll(SPACE_ID));
    for (String id : new String[] { null, "", "abc", "99a" }) {
      ExoSocialActivity activity = activity(String.valueOf(SPACE_ID));
      activity.setId(id);
      when(event.getActivity()).thenReturn(activity);

      listener.saveActivity(event);
    }

    verify(pollStorage, never()).updatePoll(any());
    verify(activityManager, never()).updateActivity(any(), anyBoolean());
  }

  /**
   * A poll that has no activity id yet is left untouched, whatever the id of
   * the activity referencing it.
   */
  @Test
  void pollWithoutActivityLeavesThePollUntouched() {
    Poll poll = poll(SPACE_ID);
    poll.setActivityId(0L);
    when(pollStorage.getPollById(POLL_ID_VALUE)).thenReturn(poll);
    ExoSocialActivity activity = activity(String.valueOf(SPACE_ID));
    activity.setId("0");
    when(event.getActivity()).thenReturn(activity);

    listener.saveActivity(event);

    verify(pollStorage, never()).updatePoll(any());
  }

  /**
   * A forged activity, here on no space stream, referencing a poll leaves it
   * untouched: its activity id isn't the poll's. Mutant: the activity id
   * comparison removed.
   */
  @Test
  void forgedActivityOnNoSpaceStreamLeavesThePollUntouched() {
    when(pollStorage.getPollById(POLL_ID_VALUE)).thenReturn(poll(SPACE_ID));
    ExoSocialActivity forged = activity(null);
    forged.setId(String.valueOf(ACTIVITY_ID + 1));
    when(event.getActivity()).thenReturn(forged);

    listener.saveActivity(event);

    verify(pollStorage, never()).updatePoll(any());
  }

  /**
   * An activity referencing a missing poll updates nothing, neither a poll
   * nor the activity. Mutant: the activity's duration param blanked and the
   * activity updated when its poll is missing.
   */
  @Test
  void activityOfAMissingPollUpdatesNothing() {
    when(pollStorage.getPollById(POLL_ID_VALUE)).thenReturn(null);
    when(event.getActivity()).thenReturn(activity(String.valueOf(SPACE_ID)));

    listener.saveActivity(event);

    verify(pollStorage, never()).updatePoll(any());
    verify(activityManager, never()).updateActivity(any(), anyBoolean());
  }

  /**
   * @param spaceId the id of the space the poll is posted in
   * @return a poll with dates in the past
   */
  private static Poll poll(long spaceId) {
    Poll poll = new Poll();
    poll.setId(POLL_ID_VALUE);
    poll.setSpaceId(spaceId);
    poll.setActivityId(ACTIVITY_ID);
    poll.setCreatedDate(new Date(1_000L));
    poll.setEndDate(new Date(2_000L));
    return poll;
  }

  /**
   * @param spaceId the id of the space whose stream holds the activity, or
   *          null for no space stream
   * @return a published scheduled poll activity referencing the poll
   */
  private static ExoSocialActivity activity(String spaceId) {
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setId(String.valueOf(ACTIVITY_ID));
    activity.setType(POLL_ACTIVITY_TYPE);
    activity.setSpaceId(spaceId);
    Map<String, String> params = new HashMap<>();
    params.put(POLL_ID, String.valueOf(POLL_ID_VALUE));
    params.put(POLL_PUBLICATION_DURATION, String.valueOf(DURATION));
    activity.setTemplateParams(params);
    return activity;
  }

}
