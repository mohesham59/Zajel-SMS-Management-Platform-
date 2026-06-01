/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
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

/**
 *
 * @author mohesham
 */


@WebServlet("/admin/stats")
public class AdminStatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Admin admin = (Admin) req.getSession().getAttribute("admin");
        if (admin == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String ctx = req.getContextPath();

        CustomerDAO cuDao = new CustomerDAO();
        MsgDAO msgDao = new MsgDAO();

        List<Customer> customers = cuDao.findAll();
        Map<Integer, Integer> counts = msgDao.getSmsCounts();

        int totalSms = 0;
        for (int c : counts.values()) totalSms += c;

        // Sort by SMS count descending
        customers.sort((a, b) -> {
            int ca = counts.getOrDefault(a.getCustomerId(), 0);
            int cb = counts.getOrDefault(b.getCustomerId(), 0);
            return Integer.compare(cb, ca);
        });

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter o = resp.getWriter();

        o.println("<!DOCTYPE html>");
        o.println("<html lang='en'>");
        o.println("<head>");
        o.println("  <meta charset='UTF-8'/>");
        o.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
        o.println("  <title>Statistics — Zajel Admin</title>");
        o.println("  <link rel='stylesheet' href='" + ctx + "/style.css'/>");
        o.println("  <style>");
        o.println(PageTemplate.headerFooterCSS());

        o.println("    .sms-count { font-family:monospace; font-weight:700; color:#4299e1; background:rgba(66,153,225,.08); padding:3px 10px; border-radius:6px; font-size:13px; }");
        o.println("    .customer-name { font-weight:700; color:#2d3748; }");
        o.println("    .pill { display:inline-flex; align-items:center; gap:6px; padding:4px 12px; border-radius:99px; font-size:11px; font-weight:700; }");
        o.println("    .pill-green { background:#ecfdf5; color:#047857; }");
        o.println("    .pill-gray  { background:#f1f5f9; color:#718096; }");
        o.println("    .pill .dot { width:6px; height:6px; border-radius:50%; background:currentColor; }");

        // Summary cards
        o.println("    .stats-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(200px,1fr)); gap:18px; margin-bottom:28px; }");
        o.println("    .stat-card { background:#fff; border:1px solid #e2e8f0; border-radius:14px;");
        o.println("      box-shadow:0 1px 3px rgba(0,0,0,.04); padding:24px; display:flex; flex-direction:column; gap:8px; }");
        o.println("    .stat-icon { width:42px; height:42px; border-radius:12px; display:flex; align-items:center; justify-content:center; font-size:18px; margin-bottom:4px; }");
        o.println("    .stat-icon.blue  { background:rgba(66,153,225,.08); }");
        o.println("    .stat-icon.red   { background:rgba(242,47,70,.08); }");
        o.println("    .stat-icon.green { background:#ecfdf5; }");
        o.println("    .stat-value { font-size:26px; font-weight:800; color:#2d3748; line-height:1; }");
        o.println("    .stat-label { font-size:12px; font-weight:600; color:#718096; text-transform:uppercase; letter-spacing:.04em; }");
        o.println("    .stat-change { font-size:12px; color:#4299e1; font-weight:600; }");

        o.println("  </style>");
        o.println("</head>");
        o.println("<body class='page-shell'>");

        // ── HEADER ───────────────────────────────
        o.println(PageTemplate.adminHeader(ctx, "Statistics"));

        // ── PAGE BODY ────────────────────────────
        o.println("<div class='page-body'>");

        o.println("  <div style='display:flex;align-items:center;justify-content:space-between;margin-bottom:28px;flex-wrap:wrap;gap:12px'>");
        o.println("    <div>");
        o.println("      <h1 class='page-title'>System Statistics</h1>");
        o.println("      <p style='color:#718096;font-size:13.5px'>Overview of all customer SMS activity.</p>");
        o.println("    </div>");
        o.println("    <span class='pill pill-green'><span class='dot'></span> Live Updates Enabled</span>");
        o.println("  </div>");

        // Summary stats
        String avgSms = customers.size() > 0
                ? String.format("%.1f", (double) totalSms / customers.size()) : "0";

        o.println("  <div class='stats-grid'>");

        o.println("    <div class='stat-card anim-fadeup'>");
        o.println("      <div class='stat-icon blue'>\uD83D\uDC65</div>");
        o.println("      <div class='stat-value'>" + customers.size() + "</div>");
        o.println("      <div class='stat-label'>Total Customers</div>");
        o.println("    </div>");

        o.println("    <div class='stat-card anim-fadeup' style='animation-delay:.06s'>");
        o.println("      <div class='stat-icon red'>\u2709</div>");
        o.println("      <div class='stat-value'>" + String.format("%,d", totalSms) + "</div>");
        o.println("      <div class='stat-label'>Total SMS Sent</div>");
        o.println("    </div>");

        o.println("    <div class='stat-card anim-fadeup' style='animation-delay:.12s'>");
        o.println("      <div class='stat-icon green'>\uD83D\uDCCA</div>");
        o.println("      <div class='stat-value'>" + avgSms + "</div>");
        o.println("      <div class='stat-label'>Avg SMS / Customer</div>");
        o.println("    </div>");

        o.println("  </div>");

        // ── STATISTICS TABLE ─────────────────────
        o.println("  <div class='card anim-fadeup' style='animation-delay:.18s'>");
        o.println("    <div style='overflow-x:auto'>");
        o.println("      <table class='tbl'>");
        o.println("        <thead>");
        o.println("          <tr>");
        o.println("            <th>Customer Account</th>");
        o.println("            <th>Email</th>");
        o.println("            <th>Status</th>");
        o.println("            <th>Total SMS Sent</th>");
        o.println("          </tr>");
        o.println("        </thead>");
        o.println("        <tbody>");

        for (Customer cu : customers) {
            int cnt = counts.getOrDefault(cu.getCustomerId(), 0);
            boolean isActive = cnt > 0;

            o.println("          <tr>");
            o.println("            <td class='customer-name'>" + Html.esc(cu.getName()) + "</td>");
            o.println("            <td style='color:#718096'>" + Html.esc(cu.getEmail()) + "</td>");
            o.println("            <td><span class='pill " + (isActive ? "pill-green" : "pill-gray") + "'><span class='dot'></span>" + (isActive ? "Active" : "Inactive") + "</span></td>");
            o.println("            <td><span class='sms-count'>" + String.format("%,d", cnt) + "</span></td>");
            o.println("          </tr>");
        }

        o.println("        </tbody>");
        o.println("      </table>");
        o.println("    </div>");
        o.println("  </div>");

        o.println("</div>"); // page-body

        // ── FOOTER ───────────────────────────────
        o.println(PageTemplate.footer());

        o.println("</body>");
        o.println("</html>");
    }
}