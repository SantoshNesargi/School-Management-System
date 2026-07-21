<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<%
String user = (String) session.getAttribute("user");
if (user == null) {
    response.sendRedirect("login.jsp");
    return;
}
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Admin Dashboard</title>

<link rel="stylesheet" href="admin.css">
</head>

<body>

<!-- ================= SIDEBAR ================= -->
<div class="sidebar">

    <h2>ADMIN</h2>

    <button onclick="showSection('add')">Add Student</button>
    <button onclick="showSection('update')">Update Student</button>
    <button onclick="showSection('delete')">Delete Student</button>
    <button onclick="showSection('view')">View Students</button>
    <button onclick="showSection('search')">Search Student</button>

    <button onclick="showSection('classview')">View By Class</button>

    <hr style="margin:10px 0;">

    <button onclick="showSection('classtt')">Add Class TT</button>
    <button onclick="showSection('viewclstt')">View Class TT</button>
    <button onclick="showSection('updateclstt')">Update Class TT</button>
    <button onclick="showSection('deleteclstt')">Delete Class TT</button>

    <hr style="margin:10px 0;">

    <button onclick="showSection('examtt')">Add Exam TT</button>
    <button onclick="showSection('viewexamtt')">View Exam TT</button>
    <button onclick="showSection('updateexamtt')">Update Exam TT</button>
    <button onclick="showSection('deleteexamtt')">Delete Exam TT</button>
    
     <hr style="margin:10px 0;">
     
      <form action="server" method="post" class="logout-form">
        <input type="hidden" name="action" value="logout">
        <button type="submit" class="logout-btn">🚪 Logout</button>
    </form>
    

</div>

<!-- ================= MAIN ================= -->
<div class="main">

<h1>Welcome Admin : <%= user %></h1>

<div id="resultArea"></div>

<!-- ================= ADD ================= -->
<div class="card hidden" id="add">
<h3>Add Student</h3>
<form action="server" method="post">
<input type="hidden" name="action" value="addStudent">

<input type="text" name="name" placeholder="Name">
<input type="text" name="class" placeholder="Class">
<input type="text" name="age" placeholder="Age">
<input type="text" name="id" placeholder="ID">

<button type="submit">Add</button>
</form>
</div>

<!-- ================= UPDATE ================= -->
<div class="card hidden" id="update">
<h3>Update Student</h3>
<form action="server" method="post">
<input type="hidden" name="action" value="updateStudent">

<input type="text" name="id" placeholder="ID">
<input type="text" name="name" placeholder="Name">
<input type="text" name="class" placeholder="Class">
<input type="text" name="age" placeholder="Age">

<button type="submit">Update</button>
</form>
</div>

<!-- ================= DELETE ================= -->
<div class="card hidden" id="delete">
<h3>Delete Student</h3>
<form action="server" method="post">
<input type="hidden" name="action" value="delete">

<input type="text" name="id" placeholder="Student ID">

<button type="submit">Delete</button>
</form>
</div>

<!-- ================= VIEW ================= -->
<div class="card hidden" id="view">
<h3>View Students</h3>
<button class="view-btn" onclick="window.location.href='server?action=viewPage'" >
Open Students Page
</button>
</div>

<!-- ================= SEARCH ================= -->
<div class="card hidden" id="search">
<h3>Search Student</h3>
<form action="server" method="get">
<input type="hidden" name="action" value="search">
<input type="number" name="id" placeholder="Student ID">
<button type="submit">Search</button>
</form>
</div>

<!-- ----------view by class--------
     (the original duplicate "search" form was collapsed into the
      existing "classview" block below — same action, same purpose) -->

<!-- ================= CLASS VIEW ================= -->
<div class="card hidden" id="classview">
<h3>View By Class</h3>
<form action="server" method="get">
<input type="hidden" name="action" value="viewByClass">
<input type="text" name="class" placeholder="Class">
<button type="submit">View</button>
</form>
</div>


<!-- ================= CLASS TT ================= -->
<div class="card hidden" id="classtt">
<h3>Add Class Timetable</h3>
<form action="server" method="post">
<input type="hidden" name="action" value="createTimetable">

<input type="text" name="class" placeholder="Class">
<input type="text" name="subject" placeholder="Subject">
<input type="date" name="day">
<input type="time" name="time">
<input type="number" name="id" placeholder="id">
<button type="submit">Create</button>
</form>
</div>

<!-- ================= VIEW CLASS TT ================= -->
<div class="card hidden" id="viewclstt">
<h3>View Class Timetable</h3>
<form action="server" method="get">
<input type="hidden" name="action" value="viewClassTTPage">
<input type="text" name="class" placeholder="Class">
<button type="submit">View</button>
</form>
</div>

<!-- ================= UPDATE CLASS TT ================= -->
<div class="card hidden" id="updateclstt">
<h3>Update Class Timetable</h3>
<form action="server" method="post">
<input type="hidden" name="action" value="updateClassTT">

<input type="text" name="id" placeholder="ID">
<input type="text" name="class" placeholder="Class">
<input type="text" name="subject" placeholder="Subject">
<input type="date" name="day">
<input type="time" name="time">

<button type="submit">Update</button>
</form>
</div>

<!-- ================= DELETE CLASS TT ================= -->
<div class="card hidden" id="deleteclstt">
<h3>Delete Class Timetable</h3>
<form action="server" method="post">
<input type="hidden" name="action" value="deleteClassTT">

<input type="text" name="class" placeholder="Class">

<button type="submit">Delete</button>
</form>
</div>

<!-- ================= EXAM TT ================= -->
<div class="card hidden" id="examtt">
<h3>Add Exam Timetable</h3>
<form action="server" method="post">
<input type="hidden" name="action" value="createExam">

<input type="text" name="class" placeholder="Class">
<input type="text" name="id" placeholder="ID">
<input type="text" name="subject" placeholder="Subject">
<input type="date" name="exam_date">
<input type="time" name="time">

<button type="submit">Create</button>
</form>
</div>

<!-- ================= VIEW EXAM TT ================= -->
<div class="card hidden" id="viewexamtt">
<h3>View Exam Timeble</h3>
<form action="server" method="get">
<input type="hidden" name="action" value="viewExamTTPage">
<input type="text" name="class" placeholder="Class">
<button type="submit">View</button>
</form>
</div>

<!-- ================= UPDATE EXAM TT ================= -->
<div class="card hidden" id="updateexamtt">
<h3>Update Exam Timetable</h3>
<form action="server" method="post">
<input type="hidden" name="action" value="updateExamTT">

<input type="text" name="id" placeholder="ID">
<input type="text" name="Class" placeholder="Class">
<input type="text" name="subject" placeholder="Subject">
<input type="date" name="date">
<input type="time" name="time">

<button type="submit">Update</button>
</form>
</div>

<!-- ================= DELETE EXAM TT ================= -->
<div class="card hidden" id="deleteexamtt">
<h3>Delete Exam Timetable</h3>
<form action="server" method="post">
<input type="hidden" name="action" value="deleteExamTT">

<input type="text" name="class" placeholder="Class">

<button type="submit">Delete</button>
</form>
</div>

</div>

<!-- -----LOGOUT  -->


  
<!-- ================= SCRIPT ================= -->
<script>
function showSection(id){
    document.querySelectorAll(".card").forEach(c => c.classList.add("hidden"));
    document.getElementById(id).classList.remove("hidden");
}
</script>

</body>
</html>