<!--
This file is part of the Meeds project (https://meeds.io/).
 
Copyright (C) 2023 Meeds Association contact@meeds.io
 
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<template>
  <v-btn
    id="pollBtnToolbar"
    :ripple="false"
    class="d-flex flex-row align-center py-2"
    text
    @click="openCreatePollDrawer">
    <v-icon
      color="amber darken-1"
      size="27">
      fa-poll
    </v-icon>
    <v-span class="body-2 font-weight-bold ms-5 dark-grey-color">
      {{ $t('poll.title') }}
    </v-span>
  </v-btn>
</template>
<script>
export default {
  props: {
    activityId: {
      type: String,
      default: null,
    },
    message: {
      type: String,
      default: null,
    },
    templateParams: {
      type: Object,
      default: null,
    },
    files: {
      type: Array,
      default: null,
    },
    activityType: {
      type: Array,
      default: null,
    },
  },
  data() {
    return {
      pollAction: 'create',
      savedPoll: {},
      pollActivity: this.activityType
    };
  },
  created() {
    document.addEventListener('create-poll-toolbar-action', this.createPollToolbarAction);
    document.addEventListener('post-activity-toolbar-action', event => {
      this.postPoll(event.detail);
    });
    document.addEventListener('message-composer-opened', () => {
      if (this.pollAction === 'update') {
        this.pollActivity.push('poll');
        document.dispatchEvent(new CustomEvent('activity-composer-edited'));
      }
    });
  },
  methods: {
    openCreatePollDrawer() {
      document.dispatchEvent(new CustomEvent('activity-composer-drawer-open', {detail: {
        activityId: this.activityId,
        activityBody: this.message,
        activityParams: this.templateParams,
        files: this.files,
        activityType: this.pollActivity,
        activityToolbarAction: true
      }}));
      window.setTimeout(() => {
        document.dispatchEvent(new CustomEvent('exo-poll-open-drawer', {detail: {activityToolbarAction: true}}));
      }, 200);
    },
    createPollToolbarAction(event) {
      Object.assign(this.savedPoll, event?.detail?.poll);
      this.pollActivity.push('poll');
      document.dispatchEvent(new CustomEvent('activity-composer-edited'));
    },
    postPoll(message) {
      if (!this.savedPoll.question || !this.savedPoll.options ) {
        return;
      }
      const poll = {
        question: this.savedPoll.question,
        options: this.savedPoll.options.filter(option => option.data != null && option.data !== '')
          .map(option => {
            return {
              description: option.data,
            };
          }),
        duration: this.savedPoll.duration,
        message: message,
        files: this.files
      };
      this.$pollService.postPoll(poll, eXo.env.portal.spaceId)
        .then((poll) => {
          const activityObject = {
            id: poll?.activityId,
            type: 'activity'
          };
          this.postSaveMessage(activityObject);
        })
        .then(() => {
          document.dispatchEvent(new CustomEvent('activity-created', {detail: this.activityId}));
          this.pollAction = 'create';
          document.dispatchEvent(new CustomEvent('update-composer-poll-label', {detail: 'create'}));
          this.savedPoll = {};
        })
        .catch(error => {
          this.$root.$emit('alert-message', this.$t('composer.poll.create.drawer.error.message', {0: error}), 'error');
        })
        .finally(() => {
          document.dispatchEvent(new CustomEvent('activity-composer-closed'));
        });
    },
    postSaveMessage(activity) {
      const postSaveOperations = extensionRegistry.loadExtensions('activity', 'saveAction');
      if (postSaveOperations?.length) {
        const promises = [];
        postSaveOperations.forEach(extension => {
          if (extension.postSave) {
            const result = extension.postSave(activity);
            if (result?.then) {
              promises.push(result);
            }
          }
        });
        return Promise.all(promises).then(() => activity);
      }
    },
  },
};
</script> 
