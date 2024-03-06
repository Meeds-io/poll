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
    :right="!$vuetify.rtl"
    allow-expand
    disable-pull-to-refresh>
    <template slot="title">
      <div class="createPollDrawerHeader">
        <span>{{ $t('composer.poll.create.drawer.label') }}</span>
      </div>
    </template>
    <template slot="content">
      <div class="pt-0 pa-5 my-5 createPollDrawerContent">
        <v-form
          class="flex"
          flat>
          <v-list
            class="d-flex flex-column"
            dense>
            <v-list-item
              class="px-0"
              dense>
              <extended-textarea
                v-model.trim="poll.question"
                rows="5"
                row-height="15"
                :max-length="MAX_LENGTH"
                :placeholder="questionPlaceholder"
                class="custom-poll-textarea pt-0 mb-3" />
            </v-list-item>
            
            <v-list-item
              v-for="(option, index) in options"
              :key="index"
              class="px-0"
              dense>
              <extended-textarea
                v-model.trim="options[index].data"
                rows="2"
                row-height="15"
                :max-length="MAX_LENGTH"
                class="custom-poll-textarea pt-0 mb-3"
                :placeholder="$t(`composer.poll.create.drawer.field.option${!option.required ? '.optional' : ''}`, {0: option.id})" />
            </v-list-item>

            <v-list-item
              class="px-0"
              dense>
              <label class="subtitle-1 font-weight-bold mb-3 mt-5">
                {{ $t('composer.poll.create.drawer.field.duration.label') }}
              </label>
            </v-list-item>

            <v-list-item
              class="px-0"
              dense>
              <select
                id="pollSelectedDuration"
                v-model="poll.duration"
                class="ignore-vuetify-classes poll-select-duration flex-grow-1">
                <option value="1day">{{ $t('composer.poll.create.drawer.field.duration.oneDay') }}</option>
                <option value="3days">{{ $t('composer.poll.create.drawer.field.duration.threeDays') }}</option>
                <option value="1week">{{ $t('composer.poll.create.drawer.field.duration.oneWeek') }}</option>
                <option value="2weeks">{{ $t('composer.poll.create.drawer.field.duration.twoWeeks') }}</option>
              </select>
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
          @click="closeDrawer">
          {{ closeButton }}
        </v-btn>
        <v-btn
          class="px-8 primary btn no-box-shadow"
          button
          large
          :disabled="disableCreatePoll"
          @click="createPoll">
          {{ submitButton }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {

  data() {
    return {
      MAX_LENGTH: 1000,
      poll: {},
      options: [],
      pollCreated: false,
      activityToolbarAction: false
    };
  },
  props: {
    savedPoll: {
      type: Object,
      default: null
    }
  },
  computed: {
    isMobile() {
      return this.$vuetify.breakpoint.name === 'xs' || this.$vuetify.breakpoint.name === 'sm';
    },
    checkPollOptionalOptions() {
      return this.options.slice(-2).every(option => !option.data || option.data.length <= this.MAX_LENGTH );
    },
    checkPollAllOptions() {
      return this.options && this.options.length !== 0 && this.options.slice(0,2).every(option => option.data !== null && option.data !== '' && option.data.length <= this.MAX_LENGTH ) && this.checkPollOptionalOptions;
    },
    disableCreatePoll() {
      return !(Object.values(this.poll).length !== 0 && this.poll.question && this.poll.question.length <= this.MAX_LENGTH && this.checkPollAllOptions);
    },
    questionPlaceholder() {
      return this.$t('composer.poll.create.drawer.field.question');
    },
    submitButton() {
      return this.$t(`composer.poll.create.drawer.action.${this.pollCreated ? 'update' : 'create'}`);
    },
    closeButton() {
      return this.$t('composer.poll.create.drawer.action.cancel');
    }
  },
  created() {
    document.addEventListener('exo-poll-open-drawer', this.openDrawer);
  },
  mounted() {
    document.querySelector('#vuetify-apps').appendChild(this.$el);
  },
  methods: {
    intializeDrawerFields() {
      this.poll = {};
      this.options = [
        {
          id: 1,
          required: true,
          data: null
        },
        {
          id: 2,
          required: true,
          data: null
        },
        {
          id: 3,
          required: false,
          data: null
        },
        {
          id: 4,
          required: false,
          data: null
        }
      ];
      this.poll.duration = '1week';
      this.pollCreated = false;
    },
    openDrawer(event) {
      if (event) {
        this.activityToolbarAction = event?.detail?.activityToolbarAction;
      }
      if (!Object.values(this.savedPoll).length) {
        this.intializeDrawerFields();
      }
      else {
        this.options = JSON.parse(JSON.stringify(this.savedPoll.options));
        Object.assign(this.poll,JSON.parse(JSON.stringify(this.savedPoll)));
      }
      this.$nextTick().then(() =>  this.$refs.createPollDrawer.open());
    },
    closeDrawer() {
      this.$refs.createPollDrawer.close();
    },
    createPoll() {
      if (!this.disableCreatePoll) {
        if (!this.poll.duration) {
          this.poll.duration = document.getElementById('pollSelectedDuration').value;
        }
        this.poll.options = this.options;
        this.pollCreated = true;
        if (this.activityToolbarAction) {
          document.dispatchEvent(new CustomEvent('create-poll-toolbar-action', {detail: {
            poll: this.poll
          }}));
          document.dispatchEvent(new CustomEvent('update-composer-poll-label', {detail: 'update'}));
        }
        this.$emit('poll-created', this.poll);
        this.closeDrawer();
      }
    },
  }
};
</script>
