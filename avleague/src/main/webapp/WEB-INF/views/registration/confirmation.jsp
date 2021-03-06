<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Players of <c:if test="${not empty newTeamName}"> ${newTeamName}</c:if><c:if test="${not empty teamName}"> ${teamName}</c:if></title>
</head>
<body>

	<h1><c:if test="${not empty newTeamName}"> ${newTeamName}</c:if><c:if test="${not empty teamName}"> ${teamName}</c:if></h1>

	<form action="end" method="POST">
	<input type="hidden" name="renamedFromTeamName" value="${renamedFromTeamName}"/>
	<input type="hidden" name="teamName" value="${teamName}"/>
	<input type="hidden" name="participatedInEarlierLeague" value="${participatedInEarlierLeague}"/>
		<ul>
			<c:forEach items="${playerList}" var="p">
				<input type="hidden" name="playerIds" value="${p.nodeId }" />
				<li><c:choose>
						<c:when test="${p.captain}">
							<strong>${p.name} (Captain)</strong>
						</c:when>
						<c:otherwise>
					${p.name}
				</c:otherwise>
					</c:choose></li>
			</c:forEach>
		</ul>
		<c:if test="${not empty matchedPlayers}">
			<p>Found similar players. May be he/she already played in one of our earlier leagues. Please confirm the correct player.</p>
		</c:if>

		<ul>
			<c:forEach var="entry" items="${matchedPlayers}">
				<li><input type="checkbox" name="players" value="${entry.key}" />${entry.key}
					<ul>
						<c:forEach var="matchedPlayer" items="${entry.value}">
							<li><input type="checkbox" name="playerIds" value="${matchedPlayer.nodeId}" /> ${matchedPlayer.name}. Played with <c:forEach
									var="playedRelation" items="${matchedPlayer.playedforInLeague}">
									<em>${playedRelation.team.name}</em> during <em><fmt:formatDate type="date" value="${playedRelation.during}" /></em>.
				 			</c:forEach></li>
						</c:forEach>
					</ul></li>
			</c:forEach>
		</ul>

		<input type="submit" value="Submit">
	</form>

</body>
</html>