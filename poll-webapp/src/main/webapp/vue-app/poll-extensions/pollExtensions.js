export function initExtensions() {
  extensionRegistry.registerExtension('ActivityComposer', 'activity-composer-action', {
    key: 'poll',
    rank: 40,
    resourceBundle: 'locale.portlet.Poll',
    labelKey: 'activity.composer.poll.create',
    description: 'activity.composer.poll.create.description',
    iconClass: 'createPollComposerIcon',
    enabled: true,
    onExecute: () => {
      return null;
    }
  });
}