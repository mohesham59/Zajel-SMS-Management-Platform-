<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.model.*" %>
<%@ page import="com.sms.util.Html" %>
<%@ page import="com.sms.util.PageTemplate" %>
<%@ page import="java.util.List" %>
<%
    Customer cu = (Customer) request.getSession().getAttribute("customer");
    String ctx = request.getContextPath();
    String success = request.getParameter("success");

    String fromSearch = request.getParameter("from");
    String toSearch = request.getParameter("to");
    String dateFrom = request.getParameter("date_from");
    String dateTo = request.getParameter("date_to");

    boolean isSearch = (fromSearch != null && !fromSearch.isEmpty())
            || (toSearch != null && !toSearch.isEmpty())
            || (dateFrom != null && !dateFrom.isEmpty())
            || (dateTo != null && !dateTo.isEmpty());

    List<Msg> msgs = (List<Msg>) request.getAttribute("msgs");
    if (msgs == null) msgs = java.util.Collections.emptyList();
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>SMS History — Zajel</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    <%= PageTemplate.headerFooterCSS() %>
    .type-pill { padding:3px 10px; border-radius:99px; font-size:11px; font-weight:700; display:inline-block; }
    .type-sent    { background:#e3f2fd; color:#1a73e8; border:1px solid #bbdefb; }
    .type-inbound { background:#ecfdf5; color:#059669; border:1px solid #a7f3d0; }
    .type-error   { background:#fef2f2; color:#dc2626; border:1px solid #fecaca; }
    .truncate { max-width:260px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; display:block; }
    .form-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(180px,1fr)); gap:14px; align-items:end; }
    .empty-state { text-align:center; padding:48px 20px; color:var(--muted); }
    .empty-state .icon { font-size:48px; margin-bottom:12px; }
    .btn-dark { background:var(--text,#2d3748); color:#fff; }
    .btn-dark:hover { background:#1a202c; }
    .mono { font-family:monospace; font-size:12px; }
  </style>
</head>
<body class='page-shell'>

<%= PageTemplate.customerHeader(ctx, "History") %>

<div class='page-body'>
  <h1 class='page-title'>SMS History</h1>
  <p style='color:var(--muted);font-size:13.5px;margin-bottom:24px'>View, search, and manage your sent messages.</p>

  <% if (success != null && !success.isEmpty()) { %>
  <div class='alert alert-ok'>✅ <%= Html.esc(success) %></div>
  <% } %>

  <div class='card anim-fadeup' style='padding:24px;margin-bottom:20px'>
    <h3 style='font-size:14px;font-weight:700;margin-bottom:18px'>Search &amp; Filter</h3>
    <form action='<%= ctx %>/customer/history' method='GET' novalidate>
      <div class='form-grid'>
        <div class='form-group' style='margin:0'><label>From</label><input type='text' name='from' placeholder='Sender…' value='<%= Html.esc(fromSearch) %>'/></div>
        <div class='form-group' style='margin:0'><label>To</label><input type='text' name='to' placeholder='Recipient…' value='<%= Html.esc(toSearch) %>'/></div>
        <div class='form-group' style='margin:0'><label>Start Date</label><input type='date' name='date_from' value='<%= Html.esc(dateFrom) %>'/></div>
        <div class='form-group' style='margin:0'><label>End Date</label><input type='date' name='date_to' value='<%= Html.esc(dateTo) %>'/></div>
        <button type='submit' class='btn btn-dark' style='height:42px'>Filter</button>
      </div>
    </form>
  </div>

  <div class='card anim-fadeup' style='animation-delay:.08s'>
    <% if (msgs.isEmpty()) { %>
    <div class='empty-state'>
      <div class='icon'>📬</div>
      <% if (isSearch) { %>
      <h3>No results found</h3><p>Try adjusting your filters.</p>
      <a href='<%= ctx %>/customer/history' class='btn btn-outline' style='margin-top:16px;text-decoration:none'>Clear</a>
      <% } else { %>
      <h3>No messages yet</h3><p>Send your first SMS.</p>
      <a href='<%= ctx %>/customer/send' class='btn btn-primary' style='margin-top:16px;text-decoration:none'>Send SMS</a>
      <% } %>
    </div>
    <% } else { %>
    <div style='padding:16px 24px;border-bottom:1px solid var(--border);display:flex;justify-content:space-between;align-items:center'>
      <span style='font-size:13px;color:var(--muted);font-weight:600'><%= msgs.size() %> message<%= (msgs.size() != 1 ? "s" : "") %></span>
      <a href='<%= ctx %>/customer/send' class='btn btn-primary btn-sm' style='text-decoration:none'>+ New SMS</a>
    </div>

    <div style='overflow-x:auto'>
      <table class='tbl'><thead><tr><th>Status</th><th>From</th><th>To</th><th>Message</th><th>Date</th><th>Actions</th></tr></thead><tbody>
      <% for (Msg m : msgs) {
          String statusPill;
          String st = m.getStatus();
          if ("SUCCESS".equals(st)) {
              statusPill = "<span class='type-pill type-sent'>SUCCESS</span>";
          } else {
              String detail = m.getErrorMsg() != null ? m.getErrorMsg() : "FAILED";
              statusPill = "<span class='type-pill type-error'>" + Html.esc(detail) + "</span>";
          }
      %>
          <tr>
            <td><%= statusPill %></td>
            <td class='mono'><%= Html.esc(cu.getMsisdn()) %></td>
            <td class='mono'><%= Html.esc(m.getReceiverMsisdn()) %></td>
            <td><span class='truncate'><%= Html.esc(m.getMsgBody()) %></span></td>
            <td style='white-space:nowrap;font-size:12px;color:var(--muted)'><%= (m.getMsgStamp() != null ? m.getMsgStamp().toString() : "—") %></td>
            <td><a href='<%= ctx %>/customer/delete-sms?id=<%= m.getMsgId() %>' class='btn btn-danger btn-sm' onclick="return confirm('Delete?')">Delete</a></td>
          </tr>
      <% } %>
      </tbody></table>
    </div>
    <% } %>
  </div>
</div>

<%= PageTemplate.footer() %>
</body>
</html>
