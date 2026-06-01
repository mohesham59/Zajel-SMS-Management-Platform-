<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.model.*" %>
<%@ page import="com.sms.util.Html" %>
<%@ page import="com.sms.util.PageTemplate" %>
<%
    Admin admin = (Admin) request.getSession().getAttribute("admin");
    String ctx = request.getContextPath();
    Customer cu = (Customer) request.getAttribute("cu");
    CustomerAddress addr = (CustomerAddress) request.getAttribute("addr");
    String error = request.getParameter("error");
    String success = request.getParameter("success");

    if (addr == null) addr = new CustomerAddress();
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>Edit Customer — Zajel Admin</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    <%= PageTemplate.headerFooterCSS() %>
    .form-grid { display:grid; grid-template-columns:1fr 1fr; gap:0 20px; }
    @media(max-width:600px) { .form-grid { grid-template-columns:1fr; } }
    .section-title { font-size:14px; font-weight:700; color:#2d3748; margin:20px 0 12px; padding-top:16px; border-top:1px solid #e2e8f0; }
    .section-title:first-of-type { border-top:none; margin-top:0; padding-top:0; }

    .form-group input:focus, .form-group select:focus, .form-group textarea:focus {
      border-color:#7c4dff !important;
      box-shadow:0 0 0 3px rgba(124,77,255,.12) !important;
    }

    .btn-save { background:#7c4dff; color:#fff; box-shadow:0 4px 14px rgba(124,77,255,.35); }
    .btn-save:hover { background:#6200ea; }
    .btn-back { background:#f1f5f9; color:#2d3748; border:1.5px solid #e2e8f0; }
    .btn-back:hover { background:#e2e8f0; }
  </style>
</head>
<body class='page-shell admin-page'>

<%= PageTemplate.adminHeader(ctx, "Manage Customers") %>

<div class='page-body'>
  <div style='margin-bottom:28px'>
    <h1 class='page-title'>✏️ Edit Customer #<%= cu.getCustomerId() %></h1>
    <p style='color:#718096;font-size:13.5px'>Update customer information and Twilio credentials.</p>
  </div>

  <% if (error != null && !error.isEmpty()) { %>
  <div class='alert alert-err' style='margin-bottom:20px'>⚠️ <%= Html.esc(error) %></div>
  <% } %>
  <% if (success != null && !success.isEmpty()) { %>
  <div class='alert alert-ok' style='margin-bottom:20px'>✅ <%= Html.esc(success) %></div>
  <% } %>

  <div class='card anim-fadeup' style='max-width:700px;padding:28px'>
    <form method='POST' action='<%= ctx %>/admin/customer-edit'>
      <input type='hidden' name='id' value='<%= cu.getCustomerId() %>'/>

      <div class='section-title'>Personal Information</div>
      <div class='form-grid'>
        <div class='form-group'><label for='name'>Name</label><input type='text' id='name' name='name' value='<%= Html.esc(cu.getName()) %>' required/></div>
        <div class='form-group'><label for='email'>Email</label><input type='email' id='email' name='email' value='<%= Html.esc(cu.getEmail()) %>' required/></div>
        <div class='form-group'><label for='msisdn'>MSISDN</label><input type='tel' id='msisdn' name='msisdn' value='<%= Html.esc(cu.getMsisdn()) %>' required/></div>
        <div class='form-group'><label for='birthday'>Birthday</label><input type='date' id='birthday' name='birthday' value='<%= Html.esc(cu.getBirthday() != null ? cu.getBirthday().toString() : "") %>'/></div>
      </div>
      <div class='form-group' style='margin-bottom:0'>
        <label for='job'>Job</label>
        <input type='text' id='job' name='job' value='<%= Html.esc(cu.getJob() != null ? cu.getJob() : "") %>'/>
      </div>

      <div class='section-title'>Address</div>
      <div class='form-grid'>
        <div class='form-group'><label for='street'>Street</label><input type='text' id='street' name='street' value='<%= Html.esc(addr.getStreet() != null ? addr.getStreet() : "") %>'/></div>
        <div class='form-group'><label for='city'>City</label><input type='text' id='city' name='city' value='<%= Html.esc(addr.getCity() != null ? addr.getCity() : "") %>'/></div>
        <div class='form-group'><label for='state'>State</label><input type='text' id='state' name='state' value='<%= Html.esc(addr.getState() != null ? addr.getState() : "") %>'/></div>
        <div class='form-group'><label for='zip'>Zip</label><input type='text' id='zip' name='zip' value='<%= Html.esc(addr.getZip() != null ? addr.getZip() : "") %>'/></div>
        <div class='form-group'><label for='country'>Country</label><input type='text' id='country' name='country' value='<%= Html.esc(addr.getCountry() != null ? addr.getCountry() : "") %>'/></div>
      </div>

      <div class='section-title'>Twilio Credentials</div>
      <div class='form-grid'>
        <div class='form-group'><label for='sid'>Account SID</label><input type='text' id='sid' name='sid' value='<%= Html.esc(cu.getSid() != null ? cu.getSid() : "") %>'/></div>
        <div class='form-group'><label for='token'>Auth Token</label><input type='text' id='token' name='token' value='<%= Html.esc(cu.getToken() != null ? cu.getToken() : "") %>'/></div>
      </div>

      <div style='display:flex;gap:10px;margin-top:24px'>
        <button type='submit' class='btn btn-save'>💾 Save Changes</button>
        <a href='<%= ctx %>/admin/customers' class='btn btn-back' style='text-decoration:none'>Cancel</a>
      </div>
    </form>
  </div>
</div>

<%= PageTemplate.footer() %>
</body>
</html>
