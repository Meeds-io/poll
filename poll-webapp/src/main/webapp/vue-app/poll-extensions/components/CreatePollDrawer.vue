<template>
  <exo-drawer
    ref="createPollDrawer"
    :drawer-width="drawerWidth"
    :right="!$vuetify.rtl"
    disable-pull-to-refresh
    @closed="resetCreatePollDrawer">
    <template slot="title">
      <div class="attachmentsDrawerHeader">
        <span>{{ $t('composer.poll.create') }}</span>
      </div>
    </template>
    <template slot="content">
      <div class="attachmentsContent pt-0 pa-5">
        <form @submit.prevent="onSubmit">
        </form>
      </div>
    </template>
    <template slot="footer">
      <div class="d-flex my-2 justify-content-end" style="justify-content : flex-end">
        <v-btn
          style="border: 1px solid #000;"
          class="mx-5 px-8"
          button
          large
          color="btn-outlined no-box-shadow"
          @click="cancelCreatePollDrawer">
          Cancel
        </v-btn>
        <v-btn
          class="px-8"
          color="primary btn no-box-shadow"
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
      // to inform that the drawer is opened and then prevent closing the activity composer drawer
      this.$root.$emit('poll-app-drawer-opened');
    },
    cancelCreatePollDrawer(){
      this.$refs.createPollDrawer.close();
      console.log('closed !');
    },
    onSubmit(){
      this.cancelCreatePollDrawer();
    },
    resetCreatePollDrawer() {
      // listner is activated in Social module 'ExoActivityComposer.vue'
      // to prevent closing the activity composer drawer
      this.$root.$emit('poll-app-drawer-closed');
    },
  }
};
</script>
