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
    <div class="actionItem" @click="openCreatePollDrawer">
      <div class="actionItemIcon">
        <div class="createPollComposerIcon"></div>
      </div>
      <div class="actionItemDescription">
        <div :class="`actionLabel ${createdPollIcon}`"> {{ pollActionLabel }} </div>
        <div class="actionDescription">
          <p>{{ pollActionDescription }}</p>
        </div>
      </div>
    </div>
    <create-poll-drawer ref="createPollDrawer" />
  </div>
</template>
<script>
export default {
  data(){
    return {
      pollAction: 'create'
    };
  },
  computed: {
    pollActionLabel(){
      return this.$t(`composer.poll.${this.pollAction}.drawer.label`);
    },
    pollActionDescription(){
      return this.$t(`composer.poll.${this.pollAction}.drawer.description`);
    },
    createdPollIcon(){
      return this.pollAction === 'update' ? 'createdPollIcon' : '';
    }
  },
  mounted(){
    this.$root.$on('poll-created',(state)=>{
      this.pollAction = state ? 'update' : 'create';
    });
  },
  methods: {
    openCreatePollDrawer() {
      this.$refs.createPollDrawer.openDrawer();
    },
  },
};
</script> 
