import CreatePollDrawer from './components/CreatePollDrawer.vue';

const components = {
  'create-poll-drawer': CreatePollDrawer,
};

for (const key in components) {
  Vue.component(key, components[key]);
}