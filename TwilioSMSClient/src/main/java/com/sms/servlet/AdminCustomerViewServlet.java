package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;
import com.sms.util.Html;
import com.sms.util.PageTemplate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/admin/customer-view")
public class AdminCustomerViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Admin admin = (Admin) req.getSession().getAttribute("admin");
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String ctx = req.getContextPath();
        int id = Integer.parseInt(req.getParameter("id"));
        Customer cu = new CustomerDAO().findById(id);
        int smsCount = new MsgDAO().countBySenderId(id);

        if (cu == null) {
            resp.sendRedirect(ctx + "/admin/customers?success=Customer+not+found");
            return;
        }

        CustomerAddress addr = cu.getCustomerAddress();
        if (addr == null) {
            addr = new CustomerAddress();
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter o = resp.getWriter();

        o.println("<!DOCTYPE html>");
        o.println("<html lang='en'>");
        o.println("<head>");
        o.println("  <meta charset='UTF-8'/>");
        o.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
        o.println("  <title>View Customer — Zajel Admin</title>");
        o.println("  <link rel='stylesheet' href='" + ctx + "/style.css'/>");
        o.println("  <style>");
        o.println(PageTemplate.headerFooterCSS());

        o.println("    .detail-grid { display:grid; grid-template-columns:1fr 1fr; gap:0 40px; }");
        o.println("    @media(max-width:600px) { .detail-grid { grid-template-columns:1fr; } }");
        o.println("    .detail-row { display:flex; align-items:center; justify-content:space-between; padding:12px 0; border-bottom:1px solid #e2e8f0; font-size:13px; }");
        o.println("    .detail-row:last-child { border-bottom:none; }");
        o.println("    .detail-key { color:#718096; font-weight:600; font-size:11px; text-transform:uppercase; letter-spacing:.04em; }");
        o.println("    .detail-val { font-family:monospace; font-size:12px; color:#2d3748; text-align:right; }");
        o.println("    .sms-count { font-family:monospace; font-weight:700; color:#7c4dff; background:rgba(124,77,255,.08); padding:3px 10px; border-radius:6px; font-size:13px; }");
        o.println("    .pill { display:inline-flex; align-items:center; gap:6px; padding:4px 12px; border-radius:99px; font-size:11px; font-weight:700; }");
        o.println("    .pill-green { background:#ecfdf5; color:#047857; }");
        o.println("    .pill .dot { width:6px; height:6px; border-radius:50%; background:currentColor; }");
        o.println("    .btn-edit { background:rgba(124,77,255,.08); color:#7c4dff; border:1.5px solid #d1c4e9; }");
        o.println("    .btn-edit:hover { background:rgba(124,77,255,.15); }");
        o.println("    .btn-back { background:#f1f5f9; color:#2d3748; border:1.5px solid #e2e8f0; }");
        o.println("    .btn-back:hover { background:#e2e8f0; }");

        o.println("  </style>");
        o.println("</head>");
        o.println("<body class='page-shell admin-page'>");

        o.println(PageTemplate.adminHeader(ctx, "Manage Customers"));

        o.println("<div class='page-body'>");

        o.println("  <div style='margin-bottom:28px'>");
        o.println("    <h1 class='page-title'>\uD83D\uDC64 Customer Details</h1>");
        o.println("    <p style='color:#718096;font-size:13.5px'>Viewing customer #" + cu.getCustomerId() + "</p>");
        o.println("  </div>");

        o.println("  <div class='card anim-fadeup' style='max-width:700px'>");
        o.println("    <div style='padding:28px'>");
        o.println("      <div class='detail-grid'>");

        o.println("        <div>");
        printRow(o, "ID", String.valueOf(cu.getCustomerId()));
        printRow(o, "Full Name", cu.getName());
        printRow(o, "Email", cu.getEmail());
        printRow(o, "MSISDN", cu.getMsisdn());
        printRow(o, "Birthday", cu.getBirthday() != null ? cu.getBirthday().toString() : "N/A");
        o.println("        </div>");

        o.println("        <div>");
        printRow(o, "Job", cu.getJob() != null ? cu.getJob() : "N/A");
        printRow(o, "Address", addr.toString());
        printRow(o, "Twilio SID", cu.getSid() != null ? cu.getSid() : "N/A");
        o.println("          <div class='detail-row'><span class='detail-key'>SMS Sent</span><span class='sms-count'>" + smsCount + "</span></div>");
        printRow(o, "Registered", cu.getCreatedAt() != null ? cu.getCreatedAt().toLocalDateTime().toLocalDate().toString() : "\u2014");
        o.println("        </div>");

        o.println("      </div>");

        o.println("      <div style='margin-top:24px;display:flex;gap:10px'>");
        o.println("        <a href='" + ctx + "/admin/customer-edit?id=" + cu.getCustomerId() + "' class='btn btn-edit' style='text-decoration:none'>\u270F\uFE0F Edit</a>");
        o.println("        <a href='" + ctx + "/admin/customers' class='btn btn-back' style='text-decoration:none'>\u2190 Back to List</a>");
        o.println("      </div>");

        o.println("    </div>");
        o.println("  </div>");
        o.println("</div>");

        o.println(PageTemplate.footer());
        o.println("</body>");
        o.println("</html>");
    }

    private void printRow(PrintWriter o, String key, String val) {
        o.println("          <div class='detail-row'><span class='detail-key'>" + Html.esc(key) + "</span><span class='detail-val'>" + Html.esc(val) + "</span></div>");
    }
}
