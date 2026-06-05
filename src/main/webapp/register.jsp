<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Register</title>
<link rel="stylesheet" href="register.css">
</head>

<body>

<div class="container">
<div class="section">

<h2>Create Account</h2>

<form action="RegisterServlet" method="post">

<label>Username</label>
<input type="text" name="username" required>

<label>Password</label>
<input type="password" name="password" required>

<br><br>

<label>Select Role</label>

<select name="role">

<option value="admin">Admin</option>

<option value="teacher">Teacher</option>

<option value="student">Student</option>

</select>

<br>

<input type="submit" value="Register">

</form>

<br>

<a href="login.jsp">Already have account? Login</a>

</div>
</div>

</body>
</html>