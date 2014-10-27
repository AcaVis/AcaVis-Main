<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Hello world!  or what?
</h1>

<P>  The time on the server is ${serverTime}. </P>


<c:forEach var="result" items="${searchResults}">
    <div><c:out value="${result.toString()}"/></div>
</c:forEach>
</body>
</html>
