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
  <exo-drawer
    ref="createPollDrawer"
    id="createPollDrawer"
    :drawer-width="drawerWidth"
    :right="!$vuetify.rtl"
    disable-pull-to-refresh
    @closed="resetDrawer">
    <template slot="title">
      <div class="createPollDrawerHeader">
        <span>{{ $t('composer.poll.create') }}</span>
      </div>
    </template>
    <template slot="content">
      <div class="pt-0 pa-5 my-5 createPollDrawerContent">
        <v-form
          class="flex"
          flat
          @submit.prevent="onSubmit">
          <v-list
            class="d-flex flex-column"
            dense>
            <v-list-item
              class="px-0"
              dense>
              <v-textarea
                counter
                maxlength="1000"
                auto-grow
                outlined
                rows="1"
                row-height="15"
                class="custom-textarea mb-3"
                :placeholder="`${$t('composer.poll.create.drawer.field.question')}`"
                type="text" />
            </v-list-item>
            
            <v-list-item
              v-for="(option, index) in options"
              :key="index"
              class="px-0 d-flex"
              dense>
              <div class=" float-left mb-8 me-3 removeOptionButton">
                <v-btn
                  v-if="option.removable"
                  icon
                  @click="removeOption(option)">
                  <em class="fas fa-trash-alt removeOptionButtonIcon"></em>
                </v-btn>
              </div> 
              <v-textarea
                counter
                maxlength="1000"
                auto-grow
                outlined
                rows="1"
                row-height="15"
                class="custom-textarea mb-3"
                :placeholder="$t('composer.poll.create.drawer.field.option', {0: option.id})"
                type="text" />
            </v-list-item>

            <v-list-item
              class="px-0 d-flex justify-end"
              dense>
              <div class="d-flex flex-row ">
                <a class="text-subtitle-1 font-weight-bold" @click="addOption">
                  + {{ $t('composer.poll.create.drawer.option.add') }}
                </a>
              </div>
            </v-list-item>
          </v-list>
        </v-form>
      </div>
    </template>
    <template slot="footer">
      <div class="d-flex my-2 flex-row justify-end">
        <v-btn
          class="mx-5 px-8 btn"
          button
          large
          @click="cancelDrawer">
          {{ $t('composer.poll.create.drawer.action.cancel') }}
        </v-btn>
        <v-btn
          class="px-8 primary btn no-box-shadow"
          button
          large
          @click="onSubmit">
          {{ $t('composer.poll.create.drawer.action.create') }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {

  data(){
    return {
      options: [
        {
          id: 1,
          removable: false,
          data: {}
        },
        {
          id: 2,
          removable: false,
          data: {}
        },
        {
          id: 3,
          removable: true,
          data: {}
        }
      ]
    };
  },

  computed: {
    isMobile() {
      return this.$vuetify.breakpoint.name === 'xs' || this.$vuetify.breakpoint.name === 'sm';
    },
    drawerWidth() {
      return !this.isMobile ? '33%' : '420';
    },
  },
  methods: {
    openDrawer(){
      this.$refs.createPollDrawer.open();
    },
    cancelDrawer(){
      this.$refs.createPollDrawer.close();
    },
    onSubmit(){
      this.cancelDrawer();
    },
    resetDrawer() {
      this.cancelDrawer();
    },
    addOption(){
      this.options.push({id: this.options.length + 1, removable: true,data: {}});
    },
    removeOption(option){
      this.options.splice(this.options.indexOf(option), 1);
      this.options.forEach((element,index) => {
        element.id = ++index;
      });
    }
  }
};
</script>
