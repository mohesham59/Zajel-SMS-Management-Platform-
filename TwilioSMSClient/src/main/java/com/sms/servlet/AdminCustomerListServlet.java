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

@WebServlet("/admin/customers")
public class AdminCustomerListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Admin admin = (Admin) req.getSession().getAttribute("admin");
        if (admin == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String ctx = req.getContextPath();
        String success = req.getParameter("success");

        List<Customer> customers = new CustomerDAO().findAll();
        Map<Integer, Integer> smsCounts = new MsgDAO().getSmsCounts();

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter o = resp.getWriter();

        o.println("<!DOCTYPE html>");
        o.println("<html lang='en'>");
        o.println("<head>");
        o.println("  <meta charset='UTF-8'/>");
        o.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
        o.println("  <title>Manage Customers — Zajel Admin</title>");
        o.println("  <link rel='stylesheet' href='" + ctx + "/style.css'/>");
        o.println("  <style>");
        o.println(PageTemplate.headerFooterCSS());

        // Purple-themed table styles
        o.println("    .user-name { font-weight:700; color:#2d3748; display:block; }");
        o.println("    .user-email { font-size:12px; color:#718096; }");
        o.println("    .sms-count { font-family:monospace; font-weight:700; color:#7c4dff; background:rgba(124,77,255,.08); padding:3px 10px; border-radius:6px; font-size:13px; }");
        o.println("    .pill { display:inline-flex; align-items:center; gap:6px; padding:4px 12px; border-radius:99px; font-size:11px; font-weight:700; }");
        o.println("    .pill-green { background:#ecfdf5; color:#047857; }");
        o.println("    .pill-gray  { background:#f1f5f9; color:#718096; }");
        o.println("    .pill .dot { width:6px; height:6px; border-radius:50%; background:currentColor; }");
        o.println("    .actions { display:flex; gap:7px; }");
        o.println("    .btn-view { background:#f1f5f9; color:#2d3748; border:1.5px solid #e2e8f0; }");
        o.println("    .btn-view:hover { background:#e2e8f0; }");
        o.println("    .btn-edit { background:rgba(124,77,255,.08); color:#7c4dff; border:1.5px solid #d1c4e9; }");
        o.println("    .btn-edit:hover { background:rgba(124,77,255,.15); }");
        o.println("    .btn-delete { background:rgba(229,62,62,.06); color:#e53e3e; border:1.5px solid #fecaca; }");
        o.println("    .btn-delete:hover { background:rgba(229,62,62,.12); }");
        o.println("    .mono { font-family:monospace; font-size:12px; }");

        o.println("  </style>");
        o.println("</head>");
        o.println("<body class='page-shell admin-page'>");

        o.println(PageTemplate.adminHeader(ctx, "Manage Customers"));

        o.println("<div class='page-body'>");

        o.println("  <div style='display:flex;align-items:center;justify-content:space-between;margin-bottom:28px;flex-wrap:wrap;gap:12px'>");
        o.println("    <div>");
        o.println("      <h1 class='page-title'>Manage Customers</h1>");
        o.println("      <p style='color:#718096;font-size:13.5px'>" + customers.size() + " registered customer" + (customers.size() != 1 ? "s" : "") + "</p>");
        o.println("    </div>");
        o.println("  </div>");

        if (success != null && !success.isEmpty()) {
            o.println("  <div class='alert alert-ok' style='margin-bottom:20px'>\u2705 " + Html.esc(success) + "</div>");
        }

        o.println("  <div class='card anim-fadeup'>");

        if (customers.isEmpty()) {
            o.println("    <div style='padding:48px;text-align:center;color:#718096'>");
            o.println("      <div style='font-size:48px;margin-bottom:12px'>\uD83D\uDC65</div>");
            o.println("      <h3>No customers registered yet</h3>");
            o.println("    </div>");
        } else {
            o.println("    <div style='overflow-x:auto'>");
            o.println("      <table class='tbl'>");
            o.println("        <thead>");
            o.println("          <tr>");
            o.println("            <th>ID</th>");
            o.println("            <th>Customer</th>");
            o.println("            <th>MSISDN</th>");
            o.println("            <th>Job</th>");
            o.println("            <th>Status</th>");
            o.println("            <th>SMS Sent</th>");
            o.println("            <th>Registered</th>");
            o.println("            <th>Actions</th>");
            o.println("          </tr>");
            o.println("        </thead>");
            o.println("        <tbody>");

            for (Customer cu : customers) {
                int cnt = smsCounts.getOrDefault(cu.getCustomerId(), 0);
                boolean isActive = cnt > 0;

                o.println("          <tr>");
                o.println("            <td style='font-weight:600'>" + cu.getCustomerId() + "</td>");
                o.println("            <td><span class='user-name'>" + Html.esc(cu.getName()) + "</span><span class='user-email'>" + Html.esc(cu.getEmail()) + "</span></td>");
                o.println("            <td class='mono'>" + Html.esc(cu.getMsisdn()) + "</td>");
                o.println("            <td>" + Html.esc(cu.getJob()) + "</td>");
                o.println("            <td><span class='pill " + (isActive ? "pill-green" : "pill-gray") + "'><span class='dot'></span>" + (isActive ? "Active" : "Inactive") + "</span></td>");
                o.println("            <td><span class='sms-count'>" + String.format("%,d", cnt) + "</span></td>");
                o.println("            <td style='white-space:nowrap;font-size:12px;color:#718096'>" + (cu.getCreatedAt() != null ? cu.getCreatedAt().toLocalDateTime().toLocalDate().toString() : "\u2014") + "</td>");
                o.println("            <td><div class='actions'>");
                o.println("              <a href='" + ctx + "/admin/customer-view?id=" + cu.getCustomerId() + "' class='btn btn-sm btn-view' style='text-decoration:none'>View</a>");
                o.println("              <a href='" + ctx + "/admin/customer-edit?id=" + cu.getCustomerId() + "' class='btn btn-sm btn-edit' style='text-decoration:none'>Edit</a>");
                o.println("              <a href='" + ctx + "/admin/customer-delete?id=" + cu.getCustomerId() + "' class='btn btn-sm btn-delete' style='text-decoration:none' onclick=\"return confirm('Delete this customer?')\">Delete</a>");
                o.println("            </div></td>");
                o.println("          </tr>");
            }

            o.println("        </tbody>");
            o.println("      </table>");
            o.println("    </div>");
        }

        o.println("  </div>");
        o.println("</div>");

        o.println(PageTemplate.footer());

        o.println("</body>");
        o.println("</html>");
    }
}