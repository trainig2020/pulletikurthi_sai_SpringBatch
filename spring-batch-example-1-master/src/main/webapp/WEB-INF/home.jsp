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
	<h1>Hello Welcome to manual and auto schedule script running form</h1>
    
    
    <a href="/load">AutoScheduling</a><br><br>
    
    <h2>Manual Mode</h2>

	<form action="/load/manualmodelist" method="post">

		<input type="datetime-local"  value="2020-10-08 19:32:00" step="1" name="datetimeloc"></br>
		</br>

		<c:forEach items="${fName}" var="fileName">
			<input type="checkbox"  name="fnames" value="${fileName}"></br>
			<c:out value="${fileName}"></c:out></br>
		</c:forEach>
		
         <input type="submit" value="Submit"> 

	</form>
	
	 <h3>File Upload:</h3>
      Select a file to upload: <br>
      <form method="post" action="/load/uploadfiles" enctype="multipart/form-data">
 
 <input type="file" name="fileUpload" size="50" />
 
 <input type="submit" value="Upload" />
 </form>
</body>
</html>