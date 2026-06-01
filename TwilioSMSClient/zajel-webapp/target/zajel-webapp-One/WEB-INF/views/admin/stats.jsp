<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.model.*" %>
<%@ page import="com.sms.util.Html" %>
<%@ page import="com.sms.util.PageTemplate" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    Admin  admin     = (Admin)  request.getSession().getAttribute("admin");
    String ctx       = request.getContextPath();
    List<Customer>       customers = (List<Customer>)       request.getAttribute("customers");
    Map<Integer,Integer> counts    = (Map<Integer,Integer>) request.getAttribute("counts");
    int    totalSms  = (Integer) request.getAttribute("totalSms");
    String avgSms    = (String)  request.getAttribute("avgSms");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Statistics — Zajel Admin</title>
  <link rel="stylesheet" href="<%= ctx %>/style.css"/>
  <style>
    <%= PageTemplate.headerFooterCSS() %>

    .sms-count { font-family:monospace; font-weight:700; color:#4299e1;
      background:rgba(66,153,225,.08); padding:3px 10px; border-radius:6px; font-size:13px; }
    .customer-name { font-weight:700; color:#2d3748; }

    .pill { display:inline-flex; align-items:center; gap:6px;
      padding:4px 12px; border-radius:99px; font-size:11px; font-weight:700; }
    .pill-green { background:#ecfdf5; color:#047857; }
    .pill-gray  { background:#f1f5f9; color:#718096; }
    .pill .dot  { width:6px; height:6px; border-radius:50%; background:currentColor; }

    .stats-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(200px,1fr));
      gap:18px; margin-bottom:28px; }
    .stat-card { background:#fff; border:1px solid #e2e8f0; border-radius:14px;
      box-shadow:0 1px 3px rgba(0,0,0,.04); padding:24px;
      display:flex; flex-direction:column; gap:8px; }
    .stat-icon { width:42px; height:42px; border-radius:12px;
      display:flex; align-items:center; justify-content:center;
      font-size:18px; margin-bottom:4px; }
    .stat-icon.blue  { background:rgba(66,153,225,.08); }
    .stat-icon.red   { background:rgba(242,47,70,.08); }
    .stat-icon.green { background:#ecfdf5; }
    .stat-value { font-size:26px; font-weight:800; color:#2d3748; line-height:1; }
    .stat-label { font-size:12px; font-weight:600; color:#718096;
      text-transform:uppercase; letter-spacing:.04em; }
  </style>
</head>
<body class="page-shell admin-page">

<%= PageTemplate.adminHeader(ctx, "Statistics") %>

<div class="page-body">

  <div style="display:flex;align-items:center;justify-content:space-between;
              margin-bottom:28px;flex-wrap:wrap;gap:12px">
    <div>
      <h1 class="page-title">System Statistics</h1>
      <p style="color:#718096;font-size:13.5px">Overview of all customer SMS activity.</p>
    </div>
    <span class="pill pill-green"><span class="dot"></span> Live Updates Enabled</span>
  </div>

  <%-- ── Summary cards ──────────────────────────────── --%>
  <div class="stats-grid">
    <div class="stat-card anim-fadeup">
      <div class="stat-icon blue">👥</div>
      <div class="stat-value"><%= customers.size() %></div>
      <div class="stat-label">Total Customers</div>
    </div>
    <div class="stat-card anim-fadeup" style="animation-delay:.06s">
      <div class="stat-icon red">✉</div>
      <div class="stat-value"><%= String.format("%,d", totalSms) %></div>
      <div class="stat-label">Total SMS Sent</div>
    </div>
    <div class="stat-card anim-fadeup" style="animation-delay:.12s">
      <div class="stat-icon green">📊</div>
      <div class="stat-value"><%= avgSms %></div>
      <div class="stat-label">Avg SMS / Customer</div>
    </div>
  </div>

  <%-- ── Statistics table ───────────────────────────── --%>
  <div class="card anim-fadeup" style="animation-delay:.18s">
    <div style="overflow-x:auto">
      <table class="tbl">
        <thead>
          <tr>
            <th>Customer Account</th>
            <th>Email</th>
            <th>Status</th>
            <th>Total SMS Sent</th>
          </tr>
        </thead>
        <tbody>
        <% for (Customer cu : customers) {
               int cnt      = counts.getOrDefault(cu.getCustomerId(), 0);
               boolean active = cnt > 0;
        %>
          <tr>
            <td class="customer-name"><%= Html.esc(cu.getName()) %></td>
            <td style="color:#718096"><%= Html.esc(cu.getEmail()) %></td>
            <td>
              <span class="pill <%= active ? "pill-green" : "pill-gray" %>">
                <span class="dot"></span>
                <%= active ? "Active" : "Inactive" %>
              </span>
            </td>
            <td><span class="sms-count"><%= String.format("%,d", cnt) %></span></td>
          </tr>
        <% } %>
        </tbody>
      </table>
    </div>
  </div>

</div>

<%= PageTemplate.footer() %>
</body>
</html>
