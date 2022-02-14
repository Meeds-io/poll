<template>
  <exo-drawer
    ref="createPollDrawer"
    :drawer-width="drawerWidth"
    :right="!$vuetify.rtl"
    disable-pull-to-refresh
    @closed="resetCreatePollDrawer">
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
            class="d-flex flex-column "
            dense>
            <v-list-item
              class="px-0"
              dense>
              <input
                placeholder="Ask something..."
                type="text"
                name="title"
                class="ignore-vuetify-classes pa-6 px-3 mb-5 createPollTextField">
            </v-list-item>
            
            <v-list-item
              v-for="(option, index) in options"
              :key="index"
              class="px-0 d-flex"
              dense>
              <div class=" float-left mb-4 me-3 removeOptionButton">
                <v-btn
                  v-if="option.removable"
                  icon
                  @click="removeOption(option)">
                  <i class="fas fa-trash-alt removeOptionButtonIcon"></i>
                </v-btn>
              </div> 
              <input
                :placeholder="`Option ${option.id}`"
                type="text"
                name="title"
                class="ignore-vuetify-classes pa-6 px-3 mb-5 createPollTextField">
            </v-list-item>

            <v-list-item
              class="px-0 d-flex justify-end"
              dense>
              <div class="d-flex flex-row ">
                <a class="text-subtitle-1 font-weight-bold" @click="addOption">
                  + Add option
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
          @click="cancelCreatePollDrawer">
          Cancel
        </v-btn>
        <v-btn
          class="px-8 primary btn no-box-shadow"
          button
          large
          @click="onSubmit">
          Create
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {

  data(){
    return {
      options: [{id: 1,removable: false,data: {}},
        {id: 2,removable: false,data: {}}]
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
  created(){
    this.$root.$on('poll-message-composer-closed', () => this.cancelCreatePollDrawer());

  },
  methods: {
    openCreatePollDrawer(){
      this.$refs.createPollDrawer.open();
      // listner is activated in Social module 'ExoActivityComposer.vue'
      // to inform that this drawer is opened and then prevent closing the activity composer drawer
      this.$root.$emit('poll-app-drawer-opened');
    },
    cancelCreatePollDrawer(){
      this.$refs.createPollDrawer.close();
    },
    onSubmit(){
      this.cancelCreatePollDrawer();
    },
    resetCreatePollDrawer() {
      // listner is activated in Social module 'ExoActivityComposer.vue'
      // to prevent closing the activity composer drawer
      this.$root.$emit('poll-app-drawer-closed');
    },
    addOption(){
      this.options.push({id: this.options.length+1,removable: true,data: {}});
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
