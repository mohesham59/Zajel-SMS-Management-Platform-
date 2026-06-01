package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;
import com.sms.util.Html;
import com.sms.util.PageTemplate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/admin/home")
public class AdminHomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Admin admin = (Admin) req.getSession().getAttribute("admin");
        if (admin == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String ctx = req.getContextPath();

        CustomerDAO cuDao = new CustomerDAO();
        MsgDAO msgDao = new MsgDAO();

        List<Customer> customers = cuDao.findAll();
        Map<Integer, Integer> smsCounts = msgDao.getSmsCounts();

        int totalCustomers = customers.size();
        int totalSms = msgDao.countAll();
        int smsToday = msgDao.countToday();
        int successCount = msgDao.countByStatus("SUCCESS");
        String deliveryRate = totalSms > 0
                ? String.format("%.1f%%", (successCount * 100.0 / totalSms))
                : "N/A";

        int activeCustomers = 0;
        for (Customer cu : customers) {
            if (smsCounts.getOrDefault(cu.getCustomerId(), 0) > 0) {
                activeCustomers++;
            }
        }

        customers.sort((a, b) -> {
            int ca = smsCounts.getOrDefault(a.getCustomerId(), 0);
            int cb = smsCounts.getOrDefault(b.getCustomerId(), 0);
            return Integer.compare(cb, ca);
        });

        int maxSms = 0;
        for (int c : smsCounts.values()) { if (c > maxSms) maxSms = c; }

        List<Log> recentLogs = LogDAO.getRecentLogs(8);

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter o = resp.getWriter();

        o.println("<!DOCTYPE html>");
        o.println("<html lang='en'>");
        o.println("<head>");
        o.println("  <meta charset='UTF-8'/>");
        o.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
        o.println("  <title>Admin Dashboard — Zajel</title>");
        o.println("  <link rel='stylesheet' href='" + ctx + "/style.css'/>");
        o.println("  <style>");
        o.println(PageTemplate.headerFooterCSS());

        // Dashboard-specific styles — PURPLE THEME
        o.println("    .stats-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(200px,1fr)); gap:18px; margin-bottom:28px; }");
        o.println("    .stat-card { background:#fff; border:1px solid #e2e8f0; border-radius:14px;");
        o.println("      box-shadow:0 1px 3px rgba(0,0,0,.04); padding:24px; display:flex; flex-direction:column; gap:8px; }");
        o.println("    .stat-icon { width:42px; height:42px; border-radius:12px; display:flex; align-items:center; justify-content:center; font-size:18px; margin-bottom:4px; }");
        o.println("    .stat-icon.purple { background:rgba(124,77,255,.08); }");
        o.println("    .stat-icon.violet { background:rgba(124,77,255,.12); }");
        o.println("    .stat-icon.green  { background:#ecfdf5; }");
        o.println("    .stat-icon.gray   { background:#f1f5f9; }");
        o.println("    .stat-value { font-size:26px; font-weight:800; color:#2d3748; line-height:1; }");
        o.println("    .stat-label { font-size:12px; font-weight:600; color:#718096; text-transform:uppercase; letter-spacing:.04em; }");
        o.println("    .stat-change { font-size:12px; color:#38a169; font-weight:600; }");
        o.println("    .stat-change.purple-color { color:#7c4dff; }");

        o.println("    .two-col { display:grid; grid-template-columns:2fr 1fr; gap:20px; }");
        o.println("    @media(max-width:900px) { .two-col { grid-template-columns:1fr; } }");

        o.println("    .card-header { display:flex; align-items:center; justify-content:space-between; padding:20px 24px; border-bottom:1px solid #e2e8f0; }");
        o.println("    .card-header h3 { font-size:14px; font-weight:700; margin:0; }");
        o.println("    .card-body { padding:0; }");

        // Bar chart — purple
        o.println("    .bar-row { display:flex; align-items:center; gap:14px; padding:13px 24px; border-bottom:1px solid #e2e8f0; font-size:13px; }");
        o.println("    .bar-row:last-child { border-bottom:none; }");
        o.println("    .bar-name { font-weight:600; color:#2d3748; width:120px; flex-shrink:0; }");
        o.println("    .bar-track { flex:1; height:8px; background:#e2e8f0; border-radius:99px; overflow:hidden; }");
        o.println("    .bar-fill { height:100%; background:#7c4dff; border-radius:99px; transition:width .6s ease; }");
        o.println("    .bar-count { font-family:monospace; font-size:12px; color:#7c4dff; font-weight:700; width:48px; text-align:right; flex-shrink:0; }");

        // Activity feed — purple accents
        o.println("    .feed-item { display:flex; align-items:flex-start; gap:12px; padding:12px 24px; border-bottom:1px solid #e2e8f0; font-size:13px; }");
        o.println("    .feed-item:last-child { border-bottom:none; }");
        o.println("    .feed-icon { width:30px; height:30px; border-radius:8px; display:flex; align-items:center; justify-content:center; font-size:13px; flex-shrink:0; }");
        o.println("    .feed-icon.sms  { background:rgba(124,77,255,.08); }");
        o.println("    .feed-icon.auth { background:#ecfdf5; }");
        o.println("    .feed-icon.warn { background:#fffbeb; }");
        o.println("    .feed-icon.info { background:rgba(124,77,255,.08); }");
        o.println("    .feed-text { flex:1; line-height:1.5; color:#2d3748; }");
        o.println("    .feed-time { font-size:11px; color:#718096; white-space:nowrap; }");

        // Customer table — purple accents
        o.println("    .sms-count { font-family:monospace; font-weight:700; color:#7c4dff; background:rgba(124,77,255,.08); padding:3px 10px; border-radius:6px; font-size:13px; }");
        o.println("    .user-name { font-weight:700; color:#2d3748; display:block; }");
        o.println("    .user-email { font-size:12px; color:#718096; }");
        o.println("    .actions { display:flex; gap:7px; }");
        o.println("    .btn-view { background:#f1f5f9; color:#2d3748; border:1.5px solid #e2e8f0; }");
        o.println("    .btn-view:hover { background:#e2e8f0; }");
        o.println("    .btn-edit { background:rgba(124,77,255,.08); color:#7c4dff; border:1.5px solid #d1c4e9; }");
        o.println("    .btn-edit:hover { background:rgba(124,77,255,.15); }");

        // Admin outline button — purple
        o.println("    .btn-outline-admin { background:transparent; color:#7c4dff; border:1.5px solid #7c4dff; }");
        o.println("    .btn-outline-admin:hover { background:rgba(124,77,255,.06); }");

        o.println("    .pill { display:inline-flex; align-items:center; gap:6px; padding:4px 12px; border-radius:99px; font-size:11px; font-weight:700; }");
        o.println("    .pill-green { background:#ecfdf5; color:#047857; }");
        o.println("    .pill-gray  { background:#f1f5f9; color:#718096; }");
        o.println("    .pill .dot { width:6px; height:6px; border-radius:50%; background:currentColor; }");

        o.println("  </style>");
        o.println("</head>");
        o.println("<body class='page-shell admin-page'>");

        // ── HEADER ───────────────────────────────
        o.println(PageTemplate.adminHeader(ctx, "Dashboard"));

        // ── PAGE BODY ────────────────────────────
        o.println("<div class='page-body'>");

        // Title row
        o.println("  <div style='display:flex;align-items:center;justify-content:space-between;margin-bottom:28px;flex-wrap:wrap;gap:12px'>");
        o.println("    <div>");
        o.println("      <h1 class='page-title'>Admin Dashboard</h1>");
        o.println("      <p style='color:#718096;font-size:13.5px'>Platform-wide overview and activity.</p>");
        o.println("    </div>");
        o.println("    <span class='pill pill-green'><span class='dot'></span> System Online</span>");
        o.println("  </div>");

        // ── STATS GRID ───────────────────────────
        o.println("  <div class='stats-grid'>");

        o.println("    <div class='stat-card anim-fadeup'>");
        o.println("      <div class='stat-icon purple'>\uD83D\uDC65</div>");
        o.println("      <div class='stat-value'>" + totalCustomers + "</div>");
        o.println("      <div class='stat-label'>Total Customers</div>");
        o.println("      <div class='stat-change purple-color'>" + activeCustomers + " Active</div>");
        o.println("    </div>");

        o.println("    <div class='stat-card anim-fadeup' style='animation-delay:.06s'>");
        o.println("      <div class='stat-icon violet'>\u2709</div>");
        o.println("      <div class='stat-value'>" + String.format("%,d", totalSms) + "</div>");
        o.println("      <div class='stat-label'>Total SMS Sent</div>");
        o.println("      <div class='stat-change'>" + String.format("%,d", successCount) + " delivered</div>");
        o.println("    </div>");

        o.println("    <div class='stat-card anim-fadeup' style='animation-delay:.12s'>");
        o.println("      <div class='stat-icon green'>\u2705</div>");
        o.println("      <div class='stat-value'>" + deliveryRate + "</div>");
        o.println("      <div class='stat-label'>Delivery Rate</div>");
        o.println("      <div class='stat-change purple-color'>Platform health: " + (successCount * 100.0 / Math.max(totalSms, 1) >= 95 ? "Good" : "Needs Attention") + "</div>");
        o.println("    </div>");

        o.println("    <div class='stat-card anim-fadeup' style='animation-delay:.18s'>");
        o.println("      <div class='stat-icon gray'>\uD83D\uDCC5</div>");
        o.println("      <div class='stat-value'>" + String.format("%,d", smsToday) + "</div>");
        o.println("      <div class='stat-label'>SMS Today</div>");
        o.println("      <div class='stat-change'>Across all accounts</div>");
        o.println("    </div>");

        o.println("  </div>");

        // ── TWO COLUMN LAYOUT ────────────────────
        o.println("  <div class='two-col'>");

        // ── LEFT COLUMN ──────────────────────────
        o.println("    <div>");

        // Top Customers by SMS Volume
        o.println("      <div class='card anim-fadeup' style='animation-delay:.2s;margin-bottom:20px'>");
        o.println("        <div class='card-header'>");
        o.println("          <h3>Top Customers by SMS Volume</h3>");
        o.println("          <a href='" + ctx + "/admin/stats' class='btn btn-sm btn-outline-admin' style='text-decoration:none'>Full Stats</a>");
        o.println("        </div>");
        o.println("        <div class='card-body'>");

        int barLimit = Math.min(customers.size(), 5);
        for (int i = 0; i < barLimit; i++) {
            Customer cu = customers.get(i);
            int cnt = smsCounts.getOrDefault(cu.getCustomerId(), 0);
            int barPct = maxSms > 0 ? (cnt * 100 / maxSms) : 0;
            String barStyle = cnt == 0 ? "width:" + barPct + "%;background:#718096" : "width:" + barPct + "%";

            o.println("          <div class='bar-row'>");
            o.println("            <span class='bar-name'>" + Html.esc(cu.getName()) + "</span>");
            o.println("            <div class='bar-track'><div class='bar-fill' style='" + barStyle + "'></div></div>");
            o.println("            <span class='bar-count'" + (cnt == 0 ? " style='color:#718096'" : "") + ">" + String.format("%,d", cnt) + "</span>");
            o.println("          </div>");
        }

        o.println("        </div>");
        o.println("      </div>");

        // Customer Accounts Table
        o.println("      <div class='card anim-fadeup' style='animation-delay:.26s'>");
        o.println("        <div class='card-header'>");
        o.println("          <h3>Customer Accounts</h3>");
        o.println("          <a href='" + ctx + "/admin/customers' class='btn btn-sm btn-outline-admin' style='text-decoration:none'>Manage All</a>");
        o.println("        </div>");
        o.println("        <div class='card-body'>");
        o.println("          <div style='overflow-x:auto'>");
        o.println("            <table class='tbl'>");
        o.println("              <thead><tr><th>Customer</th><th>Status</th><th>SMS Sent</th><th>Actions</th></tr></thead>");
        o.println("              <tbody>");

        int tableLimit = Math.min(customers.size(), 10);
        for (int i = 0; i < tableLimit; i++) {
            Customer cu = customers.get(i);
            int cnt = smsCounts.getOrDefault(cu.getCustomerId(), 0);
            boolean isActive = cnt > 0;

            o.println("                <tr>");
            o.println("                  <td><span class='user-name'>" + Html.esc(cu.getName()) + "</span><span class='user-email'>" + Html.esc(cu.getEmail()) + "</span></td>");
            o.println("                  <td><span class='pill " + (isActive ? "pill-green" : "pill-gray") + "'><span class='dot'></span>" + (isActive ? "Active" : "Inactive") + "</span></td>");
            o.println("                  <td><span class='sms-count'>" + String.format("%,d", cnt) + "</span></td>");
            o.println("                  <td><div class='actions'>");
            o.println("                    <a href='" + ctx + "/admin/customer-view?id=" + cu.getCustomerId() + "' class='btn btn-sm btn-view' style='text-decoration:none'>View</a>");
            o.println("                    <a href='" + ctx + "/admin/customer-edit?id=" + cu.getCustomerId() + "' class='btn btn-sm btn-edit' style='text-decoration:none'>Edit</a>");
            o.println("                  </div></td>");
            o.println("                </tr>");
        }

        o.println("              </tbody>");
        o.println("            </table>");
        o.println("          </div>");
        o.println("        </div>");
        o.println("      </div>");

        o.println("    </div>"); // left column

        // ── RIGHT COLUMN — Activity Feed ─────────
        o.println("    <div class='card anim-fadeup' style='animation-delay:.22s;align-self:start'>");
        o.println("      <div class='card-header'>");
        o.println("        <h3>Recent Activity</h3>");
        o.println("      </div>");
        o.println("      <div class='card-body'>");

        if (recentLogs.isEmpty()) {
            o.println("        <div style='padding:24px;text-align:center;color:#718096;font-size:13px'>No recent activity.</div>");
        } else {
            for (Log log : recentLogs) {
                String body = log.getLogBody() != null ? log.getLogBody() : "";
                String icon, iconClass;

                if (body.contains("SMS_SENT")) {
                    icon = "\u2709"; iconClass = "sms";
                } else if (body.contains("SMS_FAIL")) {
                    icon = "\u26A0\uFE0F"; iconClass = "warn";
                } else if (body.contains("LOGIN") || body.contains("REGISTER")) {
                    icon = "\uD83D\uDC64"; iconClass = "auth";
                } else if (body.contains("ADMIN")) {
                    icon = "\uD83D\uDEE0"; iconClass = "info";
                } else {
                    icon = "\uD83D\uDCCB"; iconClass = "info";
                }

                String timeStr = "\u2014";
                if (log.getLogStamp() != null) {
                    long diff = System.currentTimeMillis() - log.getLogStamp().getTime();
                    if (diff < 60000) timeStr = "Just now";
                    else if (diff < 3600000) timeStr = (diff / 60000) + "m ago";
                    else if (diff < 86400000) timeStr = (diff / 3600000) + "h ago";
                    else if (diff < 172800000) timeStr = "Yesterday";
                    else timeStr = log.getLogStamp().toLocalDateTime().toLocalDate().toString();
                }

                o.println("        <div class='feed-item'>");
                o.println("          <div class='feed-icon " + iconClass + "'>" + icon + "</div>");
                o.println("          <div style='flex:1'>");
                o.println("            <div class='feed-text'>" + Html.esc(body) + "</div>");
                o.println("            <div class='feed-time'>" + timeStr + "</div>");
                o.println("          </div>");
                o.println("        </div>");
            }
        }

        o.println("      </div>");
        o.println("    </div>");

        o.println("  </div>"); // two-col
        o.println("</div>"); // page-body

        o.println(PageTemplate.footer());

        o.println("</body>");
        o.println("</html>");
    }
}