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
%>

<script type="text/javascript">
  var pollFeatureEnabled = <%=pollFeatureEnabled%>
  if (pollFeatureEnabled) {
    require(['PORTLET/poll/PollExtensions'], app => app.init());
  }
</script>