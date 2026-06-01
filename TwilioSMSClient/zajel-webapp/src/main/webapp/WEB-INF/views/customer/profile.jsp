<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.model.*" %>
<%@ page import="com.sms.util.Html" %>
<%@ page import="com.sms.util.PageTemplate" %>
<%
    Customer cu = (Customer) request.getSession().getAttribute("customer");
    String ctx = request.getContextPath();
    String error = request.getParameter("error");
    String success = request.getParameter("success");
    CustomerAddress addr = cu.getCustomerAddress();
    if (addr == null) addr = new CustomerAddress();
    
    String safeJob = cu.getJob() != null ? cu.getJob() : "";
    String safeStreet = addr.getStreet() != null ? addr.getStreet() : "";
    String safeCity = addr.getCity() != null ? addr.getCity() : "";
    String safeState = addr.getState() != null ? addr.getState() : "";
    String safeZip = addr.getZip() != null ? addr.getZip() : "";
    String safeCountry = addr.getCountry() != null ? addr.getCountry() : "";
    String safeSid = cu.getSid() != null ? cu.getSid() : "";
    String safeToken = cu.getToken() != null ? cu.getToken() : "";
    String bday = cu.getBirthday() != null ? cu.getBirthday().toString() : "";
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>My Profile — Zajel</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    <%= PageTemplate.headerFooterCSS() %>
    .form-grid { display:grid; grid-template-columns:1fr 1fr; gap:0 20px; }
    @media(max-width:600px) { .form-grid { grid-template-columns:1fr; } }
    .section-title { font-size:14px; font-weight:700; color:#2d3748; margin:20px 0 12px; padding-top:16px; border-top:1px solid #e2e8f0; }
    .section-title:first-of-type { border-top:none; margin-top:0; padding-top:0; }
    .btn-save { background:#1a73e8; color:#fff; box-shadow:0 4px 14px rgba(26,115,232,.35); }
    .btn-save:hover { background:#0d47a1; }
    .btn-pass { background:#38a169; color:#fff; }
    .btn-pass:hover { background:#2f855a; }
  </style>
</head>
<body class='page-shell'>

<%= PageTemplate.customerHeader(ctx, "My Profile") %>

<div class='page-body'>
  <div style='margin-bottom:28px'>
    <h1 class='page-title'>👤 My Profile</h1>
    <p style='color:#718096;font-size:13.5px'>Update your personal information and Twilio credentials.</p>
  </div>

  <% if (error != null && !error.isEmpty()) { %>
  <div class='alert alert-err'>⚠️ <%= Html.esc(error) %></div>
  <% } %>
  <% if (success != null && !success.isEmpty()) { %>
  <div class='alert alert-ok'>✅ <%= Html.esc(success) %></div>
  <% } %>

  <div class='card anim-fadeup' style='max-width:700px;padding:28px;margin-bottom:20px'>
    <form method='POST' action='<%= ctx %>/customer/profile'>
      <div class='section-title'>Personal Information</div>
      <div class='form-grid'>
        <div class='form-group'><label for='name'>Name</label><input type='text' id='name' name='name' value='<%= Html.esc(cu.getName()) %>' required/></div>
        <div class='form-group'><label for='email'>Email</label><input type='email' id='email' name='email' value='<%= Html.esc(cu.getEmail()) %>' required/></div>
        <div class='form-group'><label for='msisdn'>MSISDN</label><input type='tel' id='msisdn' name='msisdn' value='<%= Html.esc(cu.getMsisdn()) %>' required/></div>
        <div class='form-group'><label for='birthday'>Birthday</label><input type='date' id='birthday' name='birthday' value='<%= Html.esc(bday) %>'/></div>
      </div>
      <div class='form-group'>
        <label for='job'>Job</label>
        <input type='text' id='job' name='job' value='<%= Html.esc(safeJob) %>'/>
      </div>

      <div class='section-title'>Address</div>
      <div class='form-grid'>
        <div class='form-group'><label for='street'>Street</label><input type='text' id='street' name='street' value='<%= Html.esc(safeStreet) %>'/></div>
        <div class='form-group'><label for='city'>City</label><input type='text' id='city' name='city' value='<%= Html.esc(safeCity) %>'/></div>
        <div class='form-group'><label for='state'>State</label><input type='text' id='state' name='state' value='<%= Html.esc(safeState) %>'/></div>
        <div class='form-group'><label for='zip'>Zip</label><input type='text' id='zip' name='zip' value='<%= Html.esc(safeZip) %>'/></div>
        <div class='form-group'><label for='country'>Country</label><input type='text' id='country' name='country' value='<%= Html.esc(safeCountry) %>'/></div>
      </div>

      <div class='section-title'>Twilio Credentials</div>
      <div class='form-grid'>
        <div class='form-group'><label for='sid'>Account SID</label><input type='text' id='sid' name='sid' value='<%= Html.esc(safeSid) %>'/></div>
        <div class='form-group'><label for='token'>Auth Token</label><input type='text' id='token' name='token' value='<%= Html.esc(safeToken) %>'/></div>
      </div>

      <button type='submit' class='btn btn-save btn-full' style='margin-top:24px'>💾 Update Profile</button>
    </form>
  </div>

  <div class='card anim-fadeup' style='max-width:700px;padding:28px;animation-delay:.08s'>
    <div class='section-title' style='border-top:none;margin-top:0;padding-top:0'>🔒 Change Password</div>
    <form method='POST' action='<%= ctx %>/customer/profile?action=password'>
      <div class='form-group'>
        <label for='new_password'>New Password</label>
        <input type='password' id='new_password' name='new_password' placeholder='Min. 6 characters' required minlength='6'/>
      </div>
      <button type='submit' class='btn btn-pass btn-full'>Change Password</button>
    </form>
  </div>
</div>

<%= PageTemplate.footer() %>
</body>
</html>
