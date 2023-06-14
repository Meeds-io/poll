const userActions = ['createPoll', 'votePoll', 'receivePollVote'];
export function init() {
  extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
    type: 'poll',
    options: {
      rank: 50,
      icon: 'fas fa-poll',
      match: (actionLabel) => userActions.includes(actionLabel),
      getLink: (realization) => {
        Vue.prototype.$set(realization, 'link', `${eXo.env.portal.context}/${eXo.env.portal.portalName}/activity?activity?id=${realization.objectId}`);
      }
    },
  });
}