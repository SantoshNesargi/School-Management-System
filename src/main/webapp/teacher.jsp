<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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
    <title>Teacher Dashboard</title>
    <link rel="stylesheet" href="teacher.css">
</head>

<body>

<div class="container">

    <h1>🎓 Teacher Dashboard</h1>

    <!-- CLASS SECTION -->
    <div class="card">

        <h3>📚 Class Panel</h3>

        <form action="teacher" method="get">
        

            <input type="text" name="class" placeholder="Enter Class (e.g. 10A)" required>

            <button type="submit" name="action" value="viewStudents" class="orange">Students</button>
            <button type="submit" name="action" value="viewExamTT" class="orange">Exam TT</button>
            <button type="submit" name="action" value="viewClassTT" class="orange">Class TT</button>
            <button type="submit" name="action" value="viewMarks" class="orange" >View Marks</button>

        </form>
    </div>

    <!-- ADD MARKS -->
    <div class="card">

        <h3>📝 Add Marks</h3>

        <form action="teacher" method="post">

            <input type="hidden" name="action" value="addMarks">

            <input type="number"
                   id="sid"
                   name="student_id"
                   placeholder="Student ID"
                   onkeyup="getStudentDetails()"
                   required>

            <input type="text" id="sname" placeholder="Student Name" readonly>
            <input type="text" id="sclass" placeholder="Class" readonly>

            <input type="text" name="subject" placeholder="Subject" required>
            <input type="number" name="marks" placeholder="Marks" required>

            <button type="submit" class="green">Add Marks</button>

        </form>
    </div>
    
    
    
    <div class="container">

  

    <!-- Update Marks -->
    <div class="card">

        <h2>✏️ Update Marks</h2>

        <form action="teacher" method="post">

            <input type="hidden" name="action" value="updateMarks">

            <input type="number"
                   name="id"
                   placeholder="Marks Record ID"
                   
                   required>
                   
                    
                    <input type="type"
                   name="subject"
                   placeholder="Subject"
                   required>
                   
                   

            <input type="number"
                   name="marks"
                   placeholder="New Marks"
                   required>
                  
            <button type="submit" class="green" >
                Update Marks
            </button>

        </form>

    </div>

    <!-- Delete Marks -->
    <div class="card" >

        <h2>🗑️ Delete Marks</h2>

        <form action="teacher" method="post"
              onsubmit="return confirm('Are you sure you want to delete this record?');">

            <input type="hidden" name="action" value="deleteMarks">

            <input type="number"
                   name="id"
                   placeholder="Marks Record ID"
                   required>

            <button type="submit" class="green">
                Delete Marks
            </button>

        </form>

    </div>

   

</div>

    <!-- ATTENDANCE -->
    <div class="card">

        <h3>📊 Add Attendance</h3>

        <form action="teacher" method="post">

            <input type="hidden" name="action" value="addAttendance">

            <input type="number" name="student_id" placeholder="Student ID" required>

            <input type="text" name="class_name" placeholder="Class (e.g. 10A)" required>

            <input type="date" name="date" required>

            <select name="status" required>
                <option value="Present">Present</option>
                <option value="Absent">Absent</option>
            </select>

            <button type="submit" class="blue">Submit Attendance</button>

        </form>
    </div>

    <!-- SUMMARY -->
    <div class="card">

        <h3>📈 Attendance Summary</h3>

        <form action="teacher" method="get">

            <input type="hidden" name="action" value="attendanceSummary">

            <input type="number" name="student_id" placeholder="Student ID" required>

            <button class="orange">Check Summary</button>

        </form>
    </div>



    <!-- LOGOUT -->
    <div class="card center">
        <a href="LogoutServlet" class="logout">🚪 Logout</a>
    </div>

</div>

<script>
function getStudentDetails() {
    let id = document.getElementById("sid").value;

    if (id === "") {
        document.getElementById("sname").value = "";
        document.getElementById("sclass").value = "";
        return;
    }

    fetch("teacher?action=getStudentName&student_id=" + id)
    .then(res => res.json())
    .then(data => {
        document.getElementById("sname").value = data.name;
        document.getElementById("sclass").value = data.class;
    })
    .catch(err => {
        console.error("getStudentDetails failed:", err);
        document.getElementById("sname").value = "";
        document.getElementById("sclass").value = "";
    });
}
</script>

</body>
</html>