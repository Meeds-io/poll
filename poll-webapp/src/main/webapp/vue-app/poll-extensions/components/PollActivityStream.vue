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
  <div id="poll-activity-stream">
    <v-card
      class="border-color border-radius my-3 pa-5"
      outlined>
      <poll-activity
        :poll="poll"
        @submit-vote="submitVote"
        :show-results="showResults"
        :final-results="finalResults" />
    </v-card>
    <div
      class="votes-remaining-state"
      v-text="remainingTime">
    </div>
  </div>
</template>

<script> 
    
export default {
  props: {
    activity: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    poll: null
  }),
  computed: {
    remainingTime() {
      const nowDateTime = new Date().getTime();
      const endDateTime = this.activity.poll.endDateTime;
      const days = this.$pollUtils.getRemainingDate.inDays(nowDateTime, endDateTime);
      const hours = this.$pollUtils.getRemainingDate.inHours(nowDateTime, endDateTime) - this.$pollUtils.getRemainingDate.inDays(nowDateTime, endDateTime)*24;
      const minutes = this.$pollUtils.getRemainingDate.inMinutes(nowDateTime, endDateTime) - this.$pollUtils.getRemainingDate.inHours(nowDateTime, endDateTime)*60;
      return this.finalResults ? this.$t('activity.poll.expired') : this.$t('activity.poll.remaining',{0: days, 1: hours, 2: minutes});
    },
    templateParams() {
      return this.activity && this.activity.templateParams;
    },
    pollId() {
      return this.templateParams && this.templateParams.pollId;
    },
    activityId() {
      return this.activity && this.activity.id;
    },
    showResults() {
      return this.activity && this.activity.poll && this.activity.poll.options && this.activity.poll.options.some(option => option.selected);
    },
    finalResults() {
      return this.activity && this.activity.poll && this.activity.poll.endDateTime < new Date().getTime();
    }
  },
  created() {
    if (this.pollId) {
      this.retrievePoll();
    }
  },
  methods: {
    retrievePoll() {
      if (this.activity.poll) {
        this.poll = this.activity.poll;
      } else {
        this.$pollService.getPollById(this.pollId)
          .then(poll => {
            this.poll = poll;
            if (!this.poll) {
              this.$root.$emit('activity-extension-abort', this.activityId);
            }
            this.activity.poll = poll;
          })
          .catch(() => {
            this.$root.$emit('activity-extension-abort', this.activityId);
          });
      }
    },
    submitVote(optionId) {
      this.$pollService.addVote(optionId, eXo.env.portal.spaceId)
        .then(() => {
          document.dispatchEvent(new CustomEvent('activity-updated', {detail: this.activityId}));
        })
        .catch(error => {
          console.error(`Error when voting: ${error}`);
        });
    }
  }
};
</script>