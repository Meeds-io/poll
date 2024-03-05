/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
export function init() {
  extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
    type: 'poll',
    options: {
      rank: 50,
      icon: 'fas fa-poll',
      match: (actionLabel) => [
        'createPoll',
        'votePoll',
        'receivePollVote'
      ].includes(actionLabel),
      getLink: (realization) => {
        realization.link = `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/activity?id=${realization.objectId}`;
        return realization.link;
      },
      isExtensible: true
    },
  });

  extensionRegistry.registerExtension('engagementCenterActions', 'activity-icon', {
    id: 'poll-icon',
    type: 'poll',
    icon: 'fas fa-poll',
    class: 'dark-yellow--color',
    title: activity => activity?.poll?.question || activity.title,
  });
}