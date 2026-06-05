<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
HttpSession session1 = request.getSession(false);

if (session1 == null || session1.getAttribute("user") == null) {
    response.sendRedirect("login.jsp");
    return;
}
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Student Management System</title>
<link rel="stylesheet"  href="style.css">
<link rel="stylesheet" href="logout.css">

</head>
<body>

<div class="container">

<h1>Student Details System</h1>

<div class="section">
<h3>Add Student</h3>
<form action="server" method="post">
    <input type="hidden" name="action" value="add">

    <label for="name">Name</label>
    <input type="text" name="name" required id="name">

    <label for="age">age</label>
    <input type="number" name="age" required id="age">

    <label for="id">ID</label>
    <input type="number" name="id" required id="id">

    <input type="submit" value="Add Student">
</form>
</div>

<div class="section">
<h3>Update Student</h3>
<form action="server" method="post">
    <input type="hidden" name="action" value="update">

    <label for="id">ID (to update)</label>
    <input type="number" name="id" required id="id">

    <label for="newname">New Name</label>
    <input type="text" name="name" required id="newname">

    <label for="newage">New age</label>
    <input type="number" name="age" required id="newage">

    <input type="submit" value="Update Student">
</form>
</div>

<div class="section">
<h3>Delete Student</h3>
<form action="server" method="post">
    <input type="hidden" name="action" value="delete">

    <label for="id">ID</label>
    <input type="number" name="id" required id="id">

    <input type="submit" value="Delete Student">
</form>
</div>

<div class="section">
<h3>Search Student</h3>
<form action="server" method="get">
    <input type="hidden" name="action" value="search">

    <label for="id">ID</label>
    <input type="number" name="id" required id="id">

    <input type="submit" value="Search Student">
</form>
</div>

<div class="section">
<h3>View All Students</h3>
<form action="server" method="get">
    <input type="hidden" name="action" value="view">

    <input type="submit" value="View Students">
</form>
</div>
<form action="LogoutServlet" method="get">
    <button class="logout-btn">Logout</button>
</form>

</div>


</body>
</html>