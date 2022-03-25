<%@ page import="org.exoplatform.commons.utils.CommonsUtils"%>
<%@page import="org.exoplatform.services.security.ConversationState"%>
<%@page import="org.exoplatform.services.security.Identity"%>

<%
boolean pollFeatureEnabled = CommonsUtils.isFeatureActive("poll");
if (ConversationState.getCurrent() != null) {
  Identity currentIdentity = ConversationState.getCurrent().getIdentity();
  if (currentIdentity != null) {
    pollFeatureEnabled = CommonsUtils.isFeatureActive("poll", currentIdentity.getUserId());    	
  }
}
String enabledSpaces = System.getProperty("exo.feature.poll.enabledSpaces", "");
%>

<script type="text/javascript">
  require(['PORTLET/poll/PollExtensions'], app => app.init(<%=pollFeatureEnabled%>, '<%=enabledSpaces%>'));
</script>