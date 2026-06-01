<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.model.*" %>
<%@ page import="com.sms.util.Html" %>
<%@ page import="com.sms.util.PageTemplate" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    Admin admin = (Admin) request.getSession().getAttribute("admin");
    String ctx = request.getContextPath();
    List<Customer> customers = (List<Customer>) request.getAttribute("customers");
    Map<Integer, Integer> smsCounts = (Map<Integer, Integer>) request.getAttribute("smsCounts");
    List<Log> recentLogs = (List<Log>) request.getAttribute("recentLogs");
    
    int totalCustomers = (Integer) request.getAttribute("totalCustomers");
    int totalSms = (Integer) request.getAttribute("totalSms");
    int smsToday = (Integer) request.getAttribute("smsToday");
    int successCount = (Integer) request.getAttribute("successCount");
    String deliveryRate = (String) request.getAttribute("deliveryRate");
    int activeCustomers = (Integer) request.getAttribute("activeCustomers");
    int maxSms = (Integer) request.getAttribute("maxSms");
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>Admin Dashboard — Zajel</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    <%= PageTemplate.headerFooterCSS() %>
    .stats-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(200px,1fr)); gap:18px; margin-bottom:28px; }
    .stat-card { background:#fff; border:1px solid #e2e8f0; border-radius:14px; box-shadow:0 1px 3px rgba(0,0,0,.04); padding:24px; display:flex; flex-direction:column; gap:8px; }
    .stat-icon { width:42px; height:42px; border-radius:12px; display:flex; align-items:center; justify-content:center; font-size:18px; margin-bottom:4px; }
    .stat-icon.purple { background:rgba(124,77,255,.08); }
    .stat-icon.violet { background:rgba(124,77,255,.12); }
    .stat-icon.green  { background:#ecfdf5; }
    .stat-icon.gray   { background:#f1f5f9; }
    .stat-value { font-size:26px; font-weight:800; color:#2d3748; line-height:1; }
    .stat-label { font-size:12px; font-weight:600; color:#718096; text-transform:uppercase; letter-spacing:.04em; }
    .stat-change { font-size:12px; color:#38a169; font-weight:600; }
    .stat-change.purple-color { color:#7c4dff; }

    .two-col { display:grid; grid-template-columns:2fr 1fr; gap:20px; }
    @media(max-width:900px) { .two-col { grid-template-columns:1fr; } }

    .card-header { display:flex; align-items:center; justify-content:space-between; padding:20px 24px; border-bottom:1px solid #e2e8f0; }
    .card-header h3 { font-size:14px; font-weight:700; margin:0; }
    .card-body { padding:0; }

    .bar-row { display:flex; align-items:center; gap:14px; padding:13px 24px; border-bottom:1px solid #e2e8f0; font-size:13px; }
    .bar-row:last-child { border-bottom:none; }
    .bar-name { font-weight:600; color:#2d3748; width:120px; flex-shrink:0; }
    .bar-track { flex:1; height:8px; background:#e2e8f0; border-radius:99px; overflow:hidden; }
    .bar-fill { height:100%; background:#7c4dff; border-radius:99px; transition:width .6s ease; }
    .bar-count { font-family:monospace; font-size:12px; color:#7c4dff; font-weight:700; width:48px; text-align:right; flex-shrink:0; }

    .feed-item { display:flex; align-items:flex-start; gap:12px; padding:12px 24px; border-bottom:1px solid #e2e8f0; font-size:13px; }
    .feed-item:last-child { border-bottom:none; }
    .feed-icon { width:30px; height:30px; border-radius:8px; display:flex; align-items:center; justify-content:center; font-size:13px; flex-shrink:0; }
    .feed-icon.sms  { background:rgba(124,77,255,.08); }
    .feed-icon.auth { background:#ecfdf5; }
    .feed-icon.warn { background:#fffbeb; }
    .feed-icon.info { background:rgba(124,77,255,.08); }
    .feed-text { flex:1; line-height:1.5; color:#2d3748; }
    .feed-time { font-size:11px; color:#718096; white-space:nowrap; }

    .sms-count { font-family:monospace; font-weight:700; color:#7c4dff; background:rgba(124,77,255,.08); padding:3px 10px; border-radius:6px; font-size:13px; }
    .user-name { font-weight:700; color:#2d3748; display:block; }
    .user-email { font-size:12px; color:#718096; }
    .actions { display:flex; gap:7px; }
    .btn-view { background:#f1f5f9; color:#2d3748; border:1.5px solid #e2e8f0; }
    .btn-view:hover { background:#e2e8f0; }
    .btn-edit { background:rgba(124,77,255,.08); color:#7c4dff; border:1.5px solid #d1c4e9; }
    .btn-edit:hover { background:rgba(124,77,255,.15); }

    .btn-outline-admin { background:transparent; color:#7c4dff; border:1.5px solid #7c4dff; }
    .btn-outline-admin:hover { background:rgba(124,77,255,.06); }

    .pill { display:inline-flex; align-items:center; gap:6px; padding:4px 12px; border-radius:99px; font-size:11px; font-weight:700; }
    .pill-green { background:#ecfdf5; color:#047857; }
    .pill-gray  { background:#f1f5f9; color:#718096; }
    .pill .dot { width:6px; height:6px; border-radius:50%; background:currentColor; }
  </style>
</head>
<body class='page-shell admin-page'>

<%= PageTemplate.adminHeader(ctx, "Dashboard") %>

<div class='page-body'>
  <div style='display:flex;align-items:center;justify-content:space-between;margin-bottom:28px;flex-wrap:wrap;gap:12px'>
    <div>
      <h1 class='page-title'>Admin Dashboard</h1>
      <p style='color:#718096;font-size:13.5px'>Platform-wide overview and activity.</p>
    </div>
    <span class='pill pill-green'><span class='dot'></span> System Online</span>
  </div>

  <div class='stats-grid'>
    <div class='stat-card anim-fadeup'>
      <div class='stat-icon purple'>👥</div>
      <div class='stat-value'><%= totalCustomers %></div>
      <div class='stat-label'>Total Customers</div>
      <div class='stat-change purple-color'><%= activeCustomers %> Active</div>
    </div>
    <div class='stat-card anim-fadeup' style='animation-delay:.06s'>
      <div class='stat-icon violet'>✉</div>
      <div class='stat-value'><%= String.format("%,d", totalSms) %></div>
      <div class='stat-label'>Total SMS Sent</div>
      <div class='stat-change'><%= String.format("%,d", successCount) %> delivered</div>
    </div>
    <div class='stat-card anim-fadeup' style='animation-delay:.12s'>
      <div class='stat-icon green'>✅</div>
      <div class='stat-value'><%= deliveryRate %></div>
      <div class='stat-label'>Delivery Rate</div>
      <div class='stat-change purple-color'>Platform health: <%= (successCount * 100.0 / Math.max(totalSms, 1) >= 95 ? "Good" : "Needs Attention") %></div>
    </div>
    <div class='stat-card anim-fadeup' style='animation-delay:.18s'>
      <div class='stat-icon gray'>📅</div>
      <div class='stat-value'><%= String.format("%,d", smsToday) %></div>
      <div class='stat-label'>SMS Today</div>
      <div class='stat-change'>Across all accounts</div>
    </div>
  </div>

  <div class='two-col'>
    <div>
      <div class='card anim-fadeup' style='animation-delay:.2s;margin-bottom:20px'>
        <div class='card-header'>
          <h3>Top Customers by SMS Volume</h3>
          <a href='<%= ctx %>/admin/stats' class='btn btn-sm btn-outline-admin' style='text-decoration:none'>Full Stats</a>
        </div>
        <div class='card-body'>
        <% int barLimit = Math.min(customers.size(), 5);
           for (int i = 0; i < barLimit; i++) {
               Customer cu = customers.get(i);
               int cnt = smsCounts.getOrDefault(cu.getCustomerId(), 0);
               int barPct = maxSms > 0 ? (cnt * 100 / maxSms) : 0;
               String barStyle = cnt == 0 ? "width:" + barPct + "%;background:#718096" : "width:" + barPct + "%";
        %>
          <div class='bar-row'>
            <span class='bar-name'><%= Html.esc(cu.getName()) %></span>
            <div class='bar-track'><div class='bar-fill' style='<%= barStyle %>'></div></div>
            <span class='bar-count' <%= (cnt == 0 ? "style='color:#718096'" : "") %>><%= String.format("%,d", cnt) %></span>
          </div>
        <% } %>
        </div>
      </div>

      <div class='card anim-fadeup' style='animation-delay:.26s'>
        <div class='card-header'>
          <h3>Customer Accounts</h3>
          <a href='<%= ctx %>/admin/customers' class='btn btn-sm btn-outline-admin' style='text-decoration:none'>Manage All</a>
        </div>
        <div class='card-body'>
          <div style='overflow-x:auto'>
            <table class='tbl'>
              <thead><tr><th>Customer</th><th>Status</th><th>SMS Sent</th><th>Actions</th></tr></thead>
              <tbody>
              <% int tableLimit = Math.min(customers.size(), 10);
                 for (int i = 0; i < tableLimit; i++) {
                     Customer cu = customers.get(i);
                     int cnt = smsCounts.getOrDefault(cu.getCustomerId(), 0);
                     boolean isActive = cnt > 0;
              %>
                <tr>
                  <td><span class='user-name'><%= Html.esc(cu.getName()) %></span><span class='user-email'><%= Html.esc(cu.getEmail()) %></span></td>
                  <td><span class='pill <%= (isActive ? "pill-green" : "pill-gray") %>'><span class='dot'></span><%= (isActive ? "Active" : "Inactive") %></span></td>
                  <td><span class='sms-count'><%= String.format("%,d", cnt) %></span></td>
                  <td><div class='actions'>
                    <a href='<%= ctx %>/admin/customer-view?id=<%= cu.getCustomerId() %>' class='btn btn-sm btn-view' style='text-decoration:none'>View</a>
                    <a href='<%= ctx %>/admin/customer-edit?id=<%= cu.getCustomerId() %>' class='btn btn-sm btn-edit' style='text-decoration:none'>Edit</a>
                  </div></td>
                </tr>
              <% } %>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <div class='card anim-fadeup' style='animation-delay:.22s;align-self:start'>
      <div class='card-header'>
        <h3>Recent Activity</h3>
      </div>
      <div class='card-body'>
      <% if (recentLogs.isEmpty()) { %>
        <div style='padding:24px;text-align:center;color:#718096;font-size:13px'>No recent activity.</div>
      <% } else {
             for (Log log : recentLogs) {
                 String body = log.getLogBody() != null ? log.getLogBody() : "";
                 String icon, iconClass;
                 if (body.contains("SMS_SENT")) { icon = "✉"; iconClass = "sms"; }
                 else if (body.contains("SMS_FAIL")) { icon = "⚠️"; iconClass = "warn"; }
                 else if (body.contains("LOGIN") || body.contains("REGISTER")) { icon = "👤"; iconClass = "auth"; }
                 else if (body.contains("ADMIN")) { icon = "🛠️"; iconClass = "info"; }
                 else { icon = "📋"; iconClass = "info"; }

                 String timeStr = "—";
                 if (log.getLogStamp() != null) {
                     long diff = System.currentTimeMillis() - log.getLogStamp().getTime();
                     if (diff < 60000) timeStr = "Just now";
                     else if (diff < 3600000) timeStr = (diff / 60000) + "m ago";
                     else if (diff < 86400000) timeStr = (diff / 3600000) + "h ago";
                     else if (diff < 172800000) timeStr = "Yesterday";
                     else timeStr = log.getLogStamp().toLocalDateTime().toLocalDate().toString();
                 }
      %>
        <div class='feed-item'>
          <div class='feed-icon <%= iconClass %>'><%= icon %></div>
          <div style='flex:1'>
            <div class='feed-text'><%= Html.esc(body) %></div>
            <div class='feed-time'><%= timeStr %></div>
          </div>
        </div>
      <%     }
         }
      %>
      </div>
    </div>
  </div>
</div>

<%= PageTemplate.footer() %>
</body>
</html>
