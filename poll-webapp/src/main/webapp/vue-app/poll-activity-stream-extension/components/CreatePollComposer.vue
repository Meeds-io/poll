<!--
This file is part of the Meeds project (https://meeds.io/).
 
Copyright (C) 2022 Meeds Association contact@meeds.io
 
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
  <div>
    <v-card
      id="createPollComposerButton"
      class="mx-4 mb-3 px-6 py-3 card-border-radius"
      outlined 
      flat 
      hover>
      <div
        class="d-flex flex-row align-center"
        @click="openCreatePollDrawer">
        <v-icon
          color="amber darken-1"
          size="50">
          fa-poll
        </v-icon>
        <v-span class="caption font-weight-bold ms-5">
          {{ pollActionLabel }}
        </v-span>
      </div>
    </v-card>
    <create-poll-drawer
      ref="createPollDrawer"
      :saved-poll="savedPoll"
      @poll-created="createPoll" />
  </div>
</template>
<script>
export default {
  data() {
    return {
      pollAction: 'create',
      savedPoll: {}
    };
  },
  props: {
    activityId: {
      type: String,
      default: null,
    },
    message: {
      type: String,
      default: null,
    },
    maxMessageLength: {
      type: Number,
      default: 0,
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
  computed: {
    pollActionLabel() {
      return this.$t(`composer.poll.${this.pollAction}.drawer.label`);
    },
  },
  created() {
    document.addEventListener('post-activity', event => {
      this.postPoll(event.detail);
    });
    document.addEventListener('message-composer-opened', () => {
      if (this.pollAction === 'update') {
        this.activityType.push('poll');
        document.dispatchEvent(new CustomEvent('activity-composer-edited'));
      }
    });
    document.addEventListener('update-composer-poll-label', event => {
      this.updateComposerPollLabel(event.detail);
    });
    document.addEventListener('activity-composer-closed', this.reset);
  },
  methods: {
    openCreatePollDrawer() {
      this.$refs.createPollDrawer.openDrawer();
    },
    updateComposerPollLabel(pollStatus) {
      this.pollAction = pollStatus;            
    },
    createPoll(poll) {
      Object.assign(this.savedPoll, poll);
      this.pollAction = 'update';
      this.activityType.push('poll');
      document.dispatchEvent(new CustomEvent('activity-composer-edited'));
    },
    postPoll(message) {
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
          this.updateComposerPollLabel(this.pollAction);
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
    reset() {
      this.pollAction = 'create';
      this.updateComposerPollLabel(this.pollAction);
      this.savedPoll = {};
    },
  },
};
</script> 
