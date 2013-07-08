<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" session="false"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Matches for League ${league.name}</title>
</head>
<body>

	<c:set var="cp" value="${pageContext.request.contextPath}" scope="application" />

	<a href="/avl">Home</a>
	<br />
	<h3>Matches for League ${league.name}</h3>

	<c:choose>
		<c:when test="${not empty league.matches}">
			<table border="1">
				<c:forEach items="${league.matches}" var="match">
					<tr valign="top">
						<td>
							<p>
								<strong><a href="${cp}/leagues/${league.name}/matches/${match.name}">${match.name} - ${match.nodeId}</a></strong>
							</p>
							<p>
								${match.level}
								<%-- <em><fmt:formatDate type="date" value="${league.startDate}" /></em> - <em><fmt:formatDate type="date" value="${league.endDate}" /></em> --%>
							</p>
							<p>${match.pool.name}</p>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<h2>No matches found.</h2>
		</c:otherwise>
	</c:choose>
</body>
</html>