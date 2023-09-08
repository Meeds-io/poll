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
import CreatePollDrawer from './components/CreatePollDrawer.vue';
import CreatePollComposer from './components/CreatePollComposer.vue';
import CreatePollToolbarAction from './components/CreatePollToolbarAction.vue';
import PollActivityStream from './components/PollActivityStream.vue';
import PollActivity from './components/PollActivity.vue';

const components = {
  'create-poll-drawer': CreatePollDrawer,
  'create-poll-composer': CreatePollComposer,
  'create-poll-toolbar-action': CreatePollToolbarAction,
  'poll-activity-stream': PollActivityStream,
  'poll-activity': PollActivity,
};

for (const key in components) {
  Vue.component(key, components[key]);
}