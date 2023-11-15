const userActions = ['createPoll', 'votePoll', 'receivePollVote'];
export function init() {
  extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
    type: 'poll',
    options: {
      rank: 50,
      icon: 'fas fa-poll',
      match: (actionLabel) => userActions.includes(actionLabel),
      getLink: (realization) => {
        realization.link = `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/activity?id=${realization.objectId}`;
        return realization.link;
      }
    },
  });
}