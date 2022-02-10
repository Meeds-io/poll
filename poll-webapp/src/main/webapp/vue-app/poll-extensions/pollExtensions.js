import * as featureService from '../../js/FeatureService.js';


var test = false
//const testEnablement = ()=>{


  featureService.isFeatureEnabled('poll').then(enabled => {
    console.log(enabled);
    test = enabled;
  });
//};
export function initExtensions() {
  extensionRegistry.registerExtension('ActivityComposer', 'activity-composer-action', {
    key: 'poll',
    rank: 40,
    resourceBundle: 'locale.portlet.Poll',
    labelKey: 'composer.poll.create',
    description: 'composer.poll.create.description',
    iconClass: 'createPollComposerIcon',
    enabled: test,
    onExecute: () => {
      return null;
    }
  });
}