<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%
String error = request.getParameter("error");
if (error != null) {
%>
    <p style="color:red;">Invalid username or password</p>
<%
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="login.css">
</head>
<body>
<div class="container">

<div class="section">

<h2>Login</h2>

<form action="LoginServlet" method="post">

<label>Username</label>
<input type="text" name="username" required>

<label>Password</label>
<input type="password" name="password" required>

<br><br>



<input type="submit" value="Login">

</form>

<br>

<a href="register.jsp">Create New Account</a>

</div>

</div>
</body>
</html>