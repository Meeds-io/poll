<%@ page import="org.exoplatform.commons.utils.CommonsUtils"%>
<%@page import="org.exoplatform.commons.api.settings.ExoFeatureService"%>

<%
ExoFeatureService featureService = CommonsUtils.getService(ExoFeatureService.class);
%>

<script type="text/javascript">
  var pollFeatureEnabled = <%=featureService.isActiveFeature("poll")%>;
  if (eXo.env.portal.spaceId && pollFeatureEnabled) {
    require(['PORTLET/poll/PollExtensions'], app => app.init());
  }
</script>