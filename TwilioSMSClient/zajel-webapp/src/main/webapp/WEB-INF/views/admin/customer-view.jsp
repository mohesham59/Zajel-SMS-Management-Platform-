<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.model.*" %>
<%@ page import="com.sms.util.Html" %>
<%@ page import="com.sms.util.PageTemplate" %>
<%
    Admin admin = (Admin) request.getSession().getAttribute("admin");
    String ctx = request.getContextPath();
    Customer cu = (Customer) request.getAttribute("cu");
    CustomerAddress addr = (CustomerAddress) request.getAttribute("addr");
    int smsCount = (Integer) request.getAttribute("smsCount");
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>View Customer — Zajel Admin</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    <%= PageTemplate.headerFooterCSS() %>
    .detail-grid { display:grid; grid-template-columns:1fr 1fr; gap:0 40px; }
    @media(max-width:600px) { .detail-grid { grid-template-columns:1fr; } }
    .detail-row { display:flex; align-items:center; justify-content:space-between; padding:12px 0; border-bottom:1px solid #e2e8f0; font-size:13px; }
    .detail-row:last-child { border-bottom:none; }
    .detail-key { color:#718096; font-weight:600; font-size:11px; text-transform:uppercase; letter-spacing:.04em; }
    .detail-val { font-family:monospace; font-size:12px; color:#2d3748; text-align:right; }
    .sms-count { font-family:monospace; font-weight:700; color:#7c4dff; background:rgba(124,77,255,.08); padding:3px 10px; border-radius:6px; font-size:13px; }
    .btn-edit { background:rgba(124,77,255,.08); color:#7c4dff; border:1.5px solid #d1c4e9; }
    .btn-edit:hover { background:rgba(124,77,255,.15); }
    .btn-back { background:#f1f5f9; color:#2d3748; border:1.5px solid #e2e8f0; }
    .btn-back:hover { background:#e2e8f0; }
  </style>
</head>
<body class='page-shell admin-page'>

<%= PageTemplate.adminHeader(ctx, "Manage Customers") %>

<div class='page-body'>
  <div style='margin-bottom:28px'>
    <h1 class='page-title'>👤 Customer Details</h1>
    <p style='color:#718096;font-size:13.5px'>Viewing customer #<%= cu.getCustomerId() %></p>
  </div>

  <div class='card anim-fadeup' style='max-width:700px'>
    <div style='padding:28px'>
      <div class='detail-grid'>
        <div>
          <div class='detail-row'><span class='detail-key'>ID</span><span class='detail-val'><%= cu.getCustomerId() %></span></div>
          <div class='detail-row'><span class='detail-key'>Full Name</span><span class='detail-val'><%= Html.esc(cu.getName()) %></span></div>
          <div class='detail-row'><span class='detail-key'>Email</span><span class='detail-val'><%= Html.esc(cu.getEmail()) %></span></div>
          <div class='detail-row'><span class='detail-key'>MSISDN</span><span class='detail-val'><%= Html.esc(cu.getMsisdn()) %></span></div>
          <div class='detail-row'><span class='detail-key'>Birthday</span><span class='detail-val'><%= cu.getBirthday() != null ? cu.getBirthday().toString() : "N/A" %></span></div>
        </div>
        <div>
          <div class='detail-row'><span class='detail-key'>Job</span><span class='detail-val'><%= Html.esc(cu.getJob() != null ? cu.getJob() : "N/A") %></span></div>
          <div class='detail-row'><span class='detail-key'>Address</span><span class='detail-val'><%= Html.esc(addr.toString()) %></span></div>
          <div class='detail-row'><span class='detail-key'>Twilio SID</span><span class='detail-val'><%= Html.esc(cu.getSid() != null ? cu.getSid() : "N/A") %></span></div>
          <div class='detail-row'><span class='detail-key'>SMS Sent</span><span class='sms-count'><%= smsCount %></span></div>
          <div class='detail-row'><span class='detail-key'>Registered</span><span class='detail-val'><%= cu.getCreatedAt() != null ? cu.getCreatedAt().toLocalDateTime().toLocalDate().toString() : "—" %></span></div>
        </div>
      </div>

      <div style='margin-top:24px;display:flex;gap:10px'>
        <a href='<%= ctx %>/admin/customer-edit?id=<%= cu.getCustomerId() %>' class='btn btn-edit' style='text-decoration:none'>✏️ Edit</a>
        <a href='<%= ctx %>/admin/customers' class='btn btn-back' style='text-decoration:none'>← Back to List</a>
      </div>
    </div>
  </div>
</div>

<%= PageTemplate.footer() %>
</body>
</html>
