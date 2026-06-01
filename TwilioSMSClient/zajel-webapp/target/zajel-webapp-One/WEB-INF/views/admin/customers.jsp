<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.model.*" %>
<%@ page import="com.sms.util.Html" %>
<%@ page import="com.sms.util.PageTemplate" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    Admin admin = (Admin) request.getSession().getAttribute("admin");
    String ctx = request.getContextPath();
    String success = request.getParameter("success");
    List<Customer> customers = (List<Customer>) request.getAttribute("customers");
    Map<Integer, Integer> smsCounts = (Map<Integer, Integer>) request.getAttribute("smsCounts");
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>Manage Customers — Zajel Admin</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    <%= PageTemplate.headerFooterCSS() %>
    .user-name { font-weight:700; color:#2d3748; display:block; }
    .user-email { font-size:12px; color:#718096; }
    .sms-count { font-family:monospace; font-weight:700; color:#7c4dff; background:rgba(124,77,255,.08); padding:3px 10px; border-radius:6px; font-size:13px; }
    .pill { display:inline-flex; align-items:center; gap:6px; padding:4px 12px; border-radius:99px; font-size:11px; font-weight:700; }
    .pill-green { background:#ecfdf5; color:#047857; }
    .pill-gray  { background:#f1f5f9; color:#718096; }
    .pill .dot { width:6px; height:6px; border-radius:50%; background:currentColor; }
    .actions { display:flex; gap:7px; }
    .btn-view { background:#f1f5f9; color:#2d3748; border:1.5px solid #e2e8f0; }
    .btn-view:hover { background:#e2e8f0; }
    .btn-edit { background:rgba(124,77,255,.08); color:#7c4dff; border:1.5px solid #d1c4e9; }
    .btn-edit:hover { background:rgba(124,77,255,.15); }
    .btn-delete { background:rgba(229,62,62,.06); color:#e53e3e; border:1.5px solid #fecaca; }
    .btn-delete:hover { background:rgba(229,62,62,.12); }
    .mono { font-family:monospace; font-size:12px; }
  </style>
</head>
<body class='page-shell admin-page'>

<%= PageTemplate.adminHeader(ctx, "Manage Customers") %>

<div class='page-body'>
  <div style='display:flex;align-items:center;justify-content:space-between;margin-bottom:28px;flex-wrap:wrap;gap:12px'>
    <div>
      <h1 class='page-title'>Manage Customers</h1>
      <p style='color:#718096;font-size:13.5px'><%= customers.size() %> registered customer<%= (customers.size() != 1 ? "s" : "") %></p>
    </div>
  </div>

  <% if (success != null && !success.isEmpty()) { %>
  <div class='alert alert-ok' style='margin-bottom:20px'>✅ <%= Html.esc(success) %></div>
  <% } %>

  <div class='card anim-fadeup'>
  <% if (customers.isEmpty()) { %>
    <div style='padding:48px;text-align:center;color:#718096'>
      <div style='font-size:48px;margin-bottom:12px'>👥</div>
      <h3>No customers registered yet</h3>
    </div>
  <% } else { %>
    <div style='overflow-x:auto'>
      <table class='tbl'>
        <thead>
          <tr>
            <th>ID</th>
            <th>Customer</th>
            <th>MSISDN</th>
            <th>Job</th>
            <th>Status</th>
            <th>SMS Sent</th>
            <th>Registered</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
        <% for (Customer cu : customers) {
            int cnt = smsCounts.getOrDefault(cu.getCustomerId(), 0);
            boolean isActive = cnt > 0;
        %>
          <tr>
            <td style='font-weight:600'><%= cu.getCustomerId() %></td>
            <td><span class='user-name'><%= Html.esc(cu.getName()) %></span><span class='user-email'><%= Html.esc(cu.getEmail()) %></span></td>
            <td class='mono'><%= Html.esc(cu.getMsisdn()) %></td>
            <td><%= Html.esc(cu.getJob()) %></td>
            <td><span class='pill <%= (isActive ? "pill-green" : "pill-gray") %>'><span class='dot'></span><%= (isActive ? "Active" : "Inactive") %></span></td>
            <td><span class='sms-count'><%= String.format("%,d", cnt) %></span></td>
            <td style='white-space:nowrap;font-size:12px;color:#718096'><%= (cu.getCreatedAt() != null ? cu.getCreatedAt().toLocalDateTime().toLocalDate().toString() : "—") %></td>
            <td><div class='actions'>
              <a href='<%= ctx %>/admin/customer-view?id=<%= cu.getCustomerId() %>' class='btn btn-sm btn-view' style='text-decoration:none'>View</a>
              <a href='<%= ctx %>/admin/customer-edit?id=<%= cu.getCustomerId() %>' class='btn btn-sm btn-edit' style='text-decoration:none'>Edit</a>
              <a href='<%= ctx %>/admin/customer-delete?id=<%= cu.getCustomerId() %>' class='btn btn-sm btn-delete' style='text-decoration:none' onclick="return confirm('Delete this customer?')">Delete</a>
            </div></td>
          </tr>
        <% } %>
        </tbody>
      </table>
    </div>
  <% } %>
  </div>
</div>

<%= PageTemplate.footer() %>
</body>
</html>
