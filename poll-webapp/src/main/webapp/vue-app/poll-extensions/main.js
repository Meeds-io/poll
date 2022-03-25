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
import {initExtensions} from './pollExtensions.js';
import './initComponents.js';
import * as pollUtils from './pollUtils.js';
import * as pollService from './pollService.js';

// getting language of the PLF
const lang = eXo.env.portal.language || 'en';
// init Vue app when locale resources are ready
const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.Poll-${lang}.json`;
// init Vue app when locale resources are ready
exoi18n.loadLanguageAsync(lang, url).then(i18n => new Vue({i18n}));

if (!Vue.prototype.$pollUtils) {
  window.Object.defineProperty(Vue.prototype, '$pollUtils', {
    value: pollUtils,
  });
}

if (!Vue.prototype.$pollService) {
  window.Object.defineProperty(Vue.prototype, '$pollService', {
    value: pollService,
  });
}

export function init(pollFeatureEnabled, enabledSpaces) {
  initExtensions(pollFeatureEnabled, enabledSpaces);
}