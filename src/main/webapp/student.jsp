<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<%
String user = (String) session.getAttribute("user");

if(user == null){
    response.sendRedirect("login.jsp");
    return;
}
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Student Dashboard</title>
<link rel="stylesheet" href="student.css">
</head>
<body>

<div class="container">

<h1>🎓 Student Dashboard</h1>

<div class="card">

<h2>Enter Student ID</h2>

<form action="student" method="get">

<input type="hidden" name="action" value="setStudent">

<input type="number"
       name="student_id"
       placeholder="Enter Student ID"
       required>

<button type="submit">
Select Student
</button>

</form>

</div>

<div class="menu">

<a href="student?action=profile">👤 My Profile</a>

<a href="student?action=marks">📝 My Marks</a>

<a href="student?action=attendance">📅 Attendance</a>

<a href="student?action=examTT">📖 Exam Timetable</a>

<a href="student?action=classTT">🏫 Class Timetable</a>

   
<a href="LogoutServlet">🚪 Logout</a>
</div>

</div>

</body>
</html>