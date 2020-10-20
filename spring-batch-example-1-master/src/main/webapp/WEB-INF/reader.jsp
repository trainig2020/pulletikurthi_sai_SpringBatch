<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Manual Schedule Form</title>
</head>
<body>
	<h1>Hello Welcome To Scheduling Of SQL</h1>


	<form action="/home/manualmode" method="get">

		<input type="datetime-local" name="selecteddate" value="2020-10-08 19:32:00" step="1"></br>
		</br>

		<c:forEach items="${ListOfFiles}" var="list">
			<input type="checkbox"  name="names" value="${list}"></br></br>
			<c:out value="${list}"></c:out></br></br>
		</c:forEach>
		
         <input type="submit" onended="Submit" value="Submit"> 
	</form>
	
</body>
</html>