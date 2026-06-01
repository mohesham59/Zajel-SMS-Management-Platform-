package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;
import com.sms.util.Html;
import com.sms.util.PageTemplate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/customer/history")
public class SmsHistoryServlet extends HttpServlet {

    // ★ Removed old NAV_BIRD SVG — now using real logo via PageTemplate

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String ctx = req.getContextPath();
        String success = req.getParameter("success");

        String fromSearch = req.getParameter("from");
        String toSearch = req.getParameter("to");
        String dateFrom = req.getParameter("date_from");
        String dateTo = req.getParameter("date_to");

        boolean isSearch = (fromSearch != null && !fromSearch.isEmpty())
                || (toSearch != null && !toSearch.isEmpty())
                || (dateFrom != null && !dateFrom.isEmpty())
                || (dateTo != null && !dateTo.isEmpty());

        MsgDAO msgDao = new MsgDAO();
        List<Msg> msgs;
        if (isSearch) {
            msgs = msgDao.search(cu.getCustomerId(), toSearch, dateFrom, dateTo);
        } else {
            msgs = msgDao.findBySenderId(cu.getCustomerId());
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter o = resp.getWriter();

        o.println("<!DOCTYPE html>");
        o.println("<html lang='en'>");
        o.println("<head>");
        o.println("  <meta charset='UTF-8'/>");
        o.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
        o.println("  <title>SMS History — Zajel</title>");
        o.println("  <link rel='stylesheet' href='" + ctx + "/style.css'/>");
        o.println("  <style>");
        o.println(PageTemplate.headerFooterCSS());
        // Blue-themed status pills
        o.println("    .type-pill { padding:3px 10px; border-radius:99px; font-size:11px; font-weight:700; display:inline-block; }");
        o.println("    .type-sent    { background:#e3f2fd; color:#1a73e8; border:1px solid #bbdefb; }");
        o.println("    .type-inbound { background:#ecfdf5; color:#059669; border:1px solid #a7f3d0; }");
        o.println("    .type-error   { background:#fef2f2; color:#dc2626; border:1px solid #fecaca; }");
        o.println("    .truncate { max-width:260px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; display:block; }");
        o.println("    .form-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(180px,1fr)); gap:14px; align-items:end; }");
        o.println("    .empty-state { text-align:center; padding:48px 20px; color:var(--muted); }");
        o.println("    .empty-state .icon { font-size:48px; margin-bottom:12px; }");
        o.println("    .btn-dark { background:var(--text,#2d3748); color:#fff; }");
        o.println("    .btn-dark:hover { background:#1a202c; }");
        o.println("    .mono { font-family:monospace; font-size:12px; }");
        o.println("  </style>");
        o.println("</head>");
        o.println("<body class='page-shell'>");

        o.println(PageTemplate.customerHeader(ctx, "History"));

        o.println("<div class='page-body'>");
        o.println("  <h1 class='page-title'>SMS History</h1>");
        o.println("  <p style='color:var(--muted);font-size:13.5px;margin-bottom:24px'>View, search, and manage your sent messages.</p>");

        if (success != null && !success.isEmpty())
            o.println("  <div class='alert alert-ok'>\u2705 " + Html.esc(success) + "</div>");

        // Search
        o.println("  <div class='card anim-fadeup' style='padding:24px;margin-bottom:20px'>");
        o.println("    <h3 style='font-size:14px;font-weight:700;margin-bottom:18px'>Search &amp; Filter</h3>");
        o.println("    <form action='" + ctx + "/customer/history' method='GET' novalidate>");
        o.println("      <div class='form-grid'>");
        o.println("        <div class='form-group' style='margin:0'><label>From</label><input type='text' name='from' placeholder='Sender\u2026' value='" + Html.esc(fromSearch) + "'/></div>");
        o.println("        <div class='form-group' style='margin:0'><label>To</label><input type='text' name='to' placeholder='Recipient\u2026' value='" + Html.esc(toSearch) + "'/></div>");
        o.println("        <div class='form-group' style='margin:0'><label>Start Date</label><input type='date' name='date_from' value='" + Html.esc(dateFrom) + "'/></div>");
        o.println("        <div class='form-group' style='margin:0'><label>End Date</label><input type='date' name='date_to' value='" + Html.esc(dateTo) + "'/></div>");
        o.println("        <button type='submit' class='btn btn-dark' style='height:42px'>Filter</button>");
        o.println("      </div>");
        o.println("    </form>");
        o.println("  </div>");

        // Results
        o.println("  <div class='card anim-fadeup' style='animation-delay:.08s'>");

        if (msgs.isEmpty()) {
            o.println("    <div class='empty-state'>");
            o.println("      <div class='icon'>\uD83D\uDCED</div>");
            if (isSearch) {
                o.println("      <h3>No results found</h3><p>Try adjusting your filters.</p>");
                o.println("      <a href='" + ctx + "/customer/history' class='btn btn-outline' style='margin-top:16px;text-decoration:none'>Clear</a>");
            } else {
                o.println("      <h3>No messages yet</h3><p>Send your first SMS.</p>");
                o.println("      <a href='" + ctx + "/customer/send' class='btn btn-primary' style='margin-top:16px;text-decoration:none'>Send SMS</a>");
            }
            o.println("    </div>");
        } else {
            o.println("    <div style='padding:16px 24px;border-bottom:1px solid var(--border);display:flex;justify-content:space-between;align-items:center'>");
            o.println("      <span style='font-size:13px;color:var(--muted);font-weight:600'>" + msgs.size() + " message" + (msgs.size() != 1 ? "s" : "") + "</span>");
            o.println("      <a href='" + ctx + "/customer/send' class='btn btn-primary btn-sm' style='text-decoration:none'>+ New SMS</a>");
            o.println("    </div>");

            o.println("    <div style='overflow-x:auto'>");
            o.println("      <table class='tbl'><thead><tr><th>Status</th><th>From</th><th>To</th><th>Message</th><th>Date</th><th>Actions</th></tr></thead><tbody>");

            for (Msg m : msgs) {
                String statusPill;
                String st = m.getStatus();
                if ("SUCCESS".equals(st)) {
                    statusPill = "<span class='type-pill type-sent'>SUCCESS</span>";
                } else {
                    String detail = m.getErrorMsg() != null ? m.getErrorMsg() : "FAILED";
                    statusPill = "<span class='type-pill type-error'>" + Html.esc(detail) + "</span>";
                }

                o.println("          <tr>");
                o.println("            <td>" + statusPill + "</td>");
                o.println("            <td class='mono'>" + Html.esc(cu.getMsisdn()) + "</td>");
                o.println("            <td class='mono'>" + Html.esc(m.getReceiverMsisdn()) + "</td>");
                o.println("            <td><span class='truncate'>" + Html.esc(m.getMsgBody()) + "</span></td>");
                o.println("            <td style='white-space:nowrap;font-size:12px;color:var(--muted)'>" + (m.getMsgStamp() != null ? m.getMsgStamp().toString() : "\u2014") + "</td>");
                o.println("            <td><a href='" + ctx + "/customer/delete-sms?id=" + m.getMsgId() + "' class='btn btn-danger btn-sm' onclick=\"return confirm('Delete?')\">Delete</a></td>");
                o.println("          </tr>");
            }

            o.println("      </tbody></table></div>");
        }

        o.println("  </div>");
        o.println("</div>");

        o.println(PageTemplate.footer());
        o.println("</body></html>");
    }
}