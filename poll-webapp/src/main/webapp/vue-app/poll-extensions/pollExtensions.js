import * as featureService from '../../js/FeatureService.js';

const testEnablement = ()=>{

  return featureService.isFeatureEnabled('poll').then(enabled => {
    console.log(enabled);
    return enabled;
  });
};
export function initExtensions() {
  extensionRegistry.registerExtension('ActivityComposer', 'activity-composer-action', {
    key: 'poll',
    rank: 40,
    resourceBundle: 'locale.portlet.Poll',
    labelKey: 'composer.poll.create',
    description: 'composer.poll.create.description',
    iconClass: 'createPollComposerIcon',
    enabled: testEnablement().then(),
    onExecute: () => {
      return null;
    }
  });
}