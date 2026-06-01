<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.model.*" %>
<%@ page import="com.sms.util.Html" %>
<%@ page import="com.sms.util.PageTemplate" %>
<%@ page import="java.util.List" %>
<%
    Customer cu = (Customer) request.getSession().getAttribute("customer");
    String ctx = request.getContextPath();
    String receiver = request.getParameter("receiver");
    String dateFrom = request.getParameter("date_from");
    String dateTo = request.getParameter("date_to");
    boolean searched = (receiver != null || dateFrom != null || dateTo != null);

    List<Msg> results = (List<Msg>) request.getAttribute("results");
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>Search SMS — Zajel</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    <%= PageTemplate.headerFooterCSS() %>
    .search-grid { display:grid; grid-template-columns:1fr 1fr 1fr; gap:14px; align-items:end; }
    @media(max-width:600px) { .search-grid { grid-template-columns:1fr; } }
    .mono { font-family:monospace; font-size:12px; }
    .type-pill { padding:3px 10px; border-radius:99px; font-size:11px; font-weight:700; display:inline-block; }
    .type-sent { background:#e3f2fd; color:#1a73e8; border:1px solid #bbdefb; }
    .type-error { background:#fef2f2; color:#dc2626; border:1px solid #fecaca; }
  </style>
</head>
<body class='page-shell'>

<%= PageTemplate.customerHeader(ctx, "Search") %>

<div class='page-body'>
  <h1 class='page-title'>🔍 Search SMS</h1>
  <p class='page-sub'>Find messages by recipient number or date range.</p>

  <div class='card anim-fadeup' style='padding:24px;margin-bottom:20px'>
    <form method='GET' action='<%= ctx %>/customer/search'>
      <div class='search-grid'>
        <div class='form-group' style='margin:0'>
          <label>Receiver MSISDN</label>
          <input type='text' name='receiver' value='<%= Html.esc(receiver) %>' placeholder='+20...'/>
        </div>
        <div class='form-group' style='margin:0'>
          <label>Date From</label>
          <input type='date' name='date_from' value='<%= Html.esc(dateFrom) %>'/>
        </div>
        <div class='form-group' style='margin:0'>
          <label>Date To</label>
          <input type='date' name='date_to' value='<%= Html.esc(dateTo) %>'/>
        </div>
      </div>
      <button class='btn btn-primary' style='margin-top:16px'>Search</button>
    </form>
  </div>

  <% if (searched && results != null) { %>
  <div class='card anim-fadeup' style='animation-delay:.06s'>
    <div style='padding:16px 24px;border-bottom:1px solid #e2e8f0;font-size:13px;font-weight:600;color:#718096'>
      <%= results.size() %> result<%= (results.size() != 1 ? "s" : "") %>
    </div>

    <% if (results.isEmpty()) { %>
    <div style='padding:48px;text-align:center;color:#718096'>
      <div style='font-size:48px;margin-bottom:12px'>📬</div>
      <h3>No messages found</h3>
    </div>
    <% } else { %>
    <div style='overflow-x:auto'>
      <table class='tbl'>
        <thead>
          <tr><th>#</th><th>From</th><th>To</th><th>Message</th><th>Date</th><th>Status</th></tr>
        </thead>
        <tbody>
        <% int i = 1; for (Msg m : results) {
            String pill = "SUCCESS".equals(m.getStatus())
                ? "<span class='type-pill type-sent'>SUCCESS</span>"
                : "<span class='type-pill type-error'>" + Html.esc(m.getErrorMsg() != null ? m.getErrorMsg() : "FAILED") + "</span>";
        %>
        <tr>
          <td><%= (i++) %></td>
          <td class='mono'><%= Html.esc(cu.getMsisdn()) %></td>
          <td class='mono'><%= Html.esc(m.getReceiverMsisdn()) %></td>
          <td><%= Html.esc(m.getMsgBody()) %></td>
          <td style='white-space:nowrap;font-size:12px;color:#718096'><%= m.getMsgStamp() %></td>
          <td><%= pill %></td>
        </tr>
        <% } %>
        </tbody>
      </table>
    </div>
    <% } %>
  </div>
  <% } %>
</div>

<%= PageTemplate.footer() %>
</body>
</html>
