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
export function initExtensions() {
  extensionRegistry.registerExtension('ActivityComposer', 'activity-composer-action', {
    key: 'poll',
    rank: 40,
    resourceBundle: 'locale.portlet.Poll',
    labelKey: 'composer.poll.create',
    description: 'composer.poll.create.drawer.description',
    iconClass: 'createPollComposerIcon',
    enabled: true,
    component: {
      name: 'create-poll-drawer',
      props: {
      },
      model: {
        value: [],
        default: []
      },
      events: []
    },
    onExecute: (createPollDrawerComponent) => {
      createPollDrawerComponent.openDrawer();
    }
  });
}