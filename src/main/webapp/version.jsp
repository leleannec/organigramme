<%@page pageEncoding="UTF-8" contentType="text/plain;charset=UTF-8"%>
<%@page import="java.net.InetAddress"%>
${project.artifactId}.version=${project.version}
${project.artifactId}.localhost.hostaddress=<%=InetAddress.getLocalHost().getHostAddress()%>
${project.artifactId}.localhost.canonicalhostname=<%=InetAddress.getLocalHost().getCanonicalHostName()%>
${project.artifactId}.localhost.hostname=<%=InetAddress.getLocalHost().getHostName()%>
<%
	HttpSession theSession = request.getSession(false);

	// print out the session id
	try {
		if (theSession != null) {
			synchronized (theSession) {
				// invalidating a session destroys it
				theSession.invalidate();
			}
		}
	} catch (Exception e) {
		// ignored
	}
%>
