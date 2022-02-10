import {initExtensions} from './pollExtensions.js';

//getting language of the PLF
const lang = eXo && eXo.env.portal.language || 'en';

//should expose the locale ressources as REST API
const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.Poll-${lang}.json`;

export function init() {
  exoi18n.loadLanguageAsync(lang, url).then(() => {
    initExtensions();
  });
}