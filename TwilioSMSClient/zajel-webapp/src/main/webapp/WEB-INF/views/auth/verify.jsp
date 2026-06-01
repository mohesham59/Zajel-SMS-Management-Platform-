<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.util.Html" %>
<%
    String error = request.getParameter("error");
    String info = request.getParameter("info");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>Verify — Zajel</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    body { padding: 40px 20px 60px; background:var(--navy); }
    .center-box {
      display: flex; justify-content: center; align-items: center; height: 100vh;
    }
    .card { background: var(--white); border: 1px solid var(--border); border-radius: 14px; box-shadow: 0 1px 3px rgba(0,0,0,.04); padding: 40px; width: 100%; max-width: 400px; }
    .btn-green { background: #38a169; color: white; }
  </style>
</head>
<body>
<div class='center-box'>
  <div class='card login-card'>
    <h2 style='text-align:center'>🔐 Verify Account</h2>
    <p style='text-align:center;color:#666;margin-bottom:18px'>Enter the 6-digit code sent to your phone</p>
    <% if (error != null) { %>
        <div class='alert alert-err'><%= Html.esc(error) %></div>
    <% } %>
    <% if (info != null) { %>
        <div class='alert alert-ok'><%= Html.esc(info) %></div>
    <% } %>
    <form method='POST' action='<%= ctx %>/verify'>
      <label>Verification Code</label>
      <input type='text' name='code' required maxlength='6' style='text-align:center;font-size:24px;letter-spacing:8px' placeholder='------'>
      <button type='submit' class='btn' style='width:100%'>Verify</button>
    </form>
    <form method='POST' action='<%= ctx %>/verify?action=resend' style='margin-top:12px'>
      <button type='submit' class='btn btn-green' style='width:100%'>Resend Code</button>
    </form>
  </div>
</div>
</body>
</html>
