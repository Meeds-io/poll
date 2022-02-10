// get overridden components if exists
if (extensionRegistry) {
  const components = extensionRegistry.loadComponents('Poll');
  if (components && components.length > 0) {
    components.forEach(cmp => {
      Vue.component(cmp.componentName, cmp.componentOptions);
    });
  }
}