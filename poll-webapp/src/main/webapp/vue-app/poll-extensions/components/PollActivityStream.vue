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
      <poll-activity :activity="activity" @submit-vote="submitVote" />
    </v-card>
    <div
      class="votes-remaining-state"
      v-text="reminingTime">
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
  data() {
    return {
      d1: new Date(),
      d2: new Date('3/20/2022')
    };
  },
  computed: {
    reminingTime() {
      const days = this.$pollUtils.getRemainingDate.inDays(this.d1, this.d2);
      const hours = this.$pollUtils.getRemainingDate.inHours(this.d1, this.d2)-this.$pollUtils.getRemainingDate.inDays(this.d1, this.d2)*24;
      const minutes = this.$pollUtils.getRemainingDate.inMunites(this.d1, this.d2)-this.$pollUtils.getRemainingDate.inHours(this.d1, this.d2)*60;
      return this.$t('activity.poll.remaining',{0: days, 1: hours, 2: minutes});
    },
  },
  methods: {
    submitVote(){
      return;
    }
  }
};
</script>