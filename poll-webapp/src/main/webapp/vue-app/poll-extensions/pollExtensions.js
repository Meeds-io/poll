export function initExtensions() {
  extensionRegistry.registerExtension('ActivityComposer', 'activity-composer-action', {
    key: 'poll',
    rank: 40,
    resourceBundle: 'locale.portlet.Poll',
    labelKey: 'composer.poll.create',
    description: 'composer.poll.create.description',
    iconClass: 'createPollComposerIcon',
    enabled: true,
    onExecute: () => {
      return null;
    }
  });
}