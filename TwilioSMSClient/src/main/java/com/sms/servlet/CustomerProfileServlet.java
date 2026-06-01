package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;
import com.sms.util.Html;
import com.sms.util.PageTemplate;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Date;

@WebServlet("/customer/profile")
public class CustomerProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        cu = new CustomerDAO().findById(cu.getCustomerId());
        req.getSession().setAttribute("customer", cu);

        String ctx     = req.getContextPath();
        String error   = req.getParameter("error");
        String success = req.getParameter("success");
        CustomerAddress addr = cu.getCustomerAddress();
        if (addr == null) addr = new CustomerAddress();

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter o = resp.getWriter();

        o.println("<!DOCTYPE html>");
        o.println("<html lang='en'>");
        o.println("<head>");
        o.println("  <meta charset='UTF-8'/>");
        o.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
        o.println("  <title>My Profile — Zajel</title>");
        o.println("  <link rel='stylesheet' href='" + ctx + "/style.css'/>");
        o.println("  <style>");
        o.println(PageTemplate.headerFooterCSS());

        o.println("    .form-grid { display:grid; grid-template-columns:1fr 1fr; gap:0 20px; }");
        o.println("    @media(max-width:600px) { .form-grid { grid-template-columns:1fr; } }");
        o.println("    .section-title { font-size:14px; font-weight:700; color:#2d3748; margin:20px 0 12px; padding-top:16px; border-top:1px solid #e2e8f0; }");
        o.println("    .section-title:first-of-type { border-top:none; margin-top:0; padding-top:0; }");
        o.println("    .btn-save { background:#1a73e8; color:#fff; box-shadow:0 4px 14px rgba(26,115,232,.35); }");
        o.println("    .btn-save:hover { background:#0d47a1; }");
        o.println("    .btn-pass { background:#38a169; color:#fff; }");
        o.println("    .btn-pass:hover { background:#2f855a; }");

        o.println("  </style>");
        o.println("</head>");
        o.println("<body class='page-shell'>");

        o.println(PageTemplate.customerHeader(ctx, "My Profile"));

        o.println("<div class='page-body'>");

        o.println("  <div style='margin-bottom:28px'>");
        o.println("    <h1 class='page-title'>\uD83D\uDC64 My Profile</h1>");
        o.println("    <p style='color:#718096;font-size:13.5px'>Update your personal information and Twilio credentials.</p>");
        o.println("  </div>");

        if (error != null && !error.isEmpty())
            o.println("  <div class='alert alert-err'>\u26A0\uFE0F " + Html.esc(error) + "</div>");
        if (success != null && !success.isEmpty())
            o.println("  <div class='alert alert-ok'>\u2705 " + Html.esc(success) + "</div>");

        // ── Profile Form ──
        o.println("  <div class='card anim-fadeup' style='max-width:700px;padding:28px;margin-bottom:20px'>");
        o.println("    <form method='POST' action='" + ctx + "/customer/profile'>");

        o.println("      <div class='section-title'>Personal Information</div>");
        o.println("      <div class='form-grid'>");
        field(o, "Name", "name", "text", cu.getName(), true);
        field(o, "Email", "email", "email", cu.getEmail(), true);
        field(o, "MSISDN", "msisdn", "tel", cu.getMsisdn(), true);
        field(o, "Birthday", "birthday", "date", cu.getBirthday() != null ? cu.getBirthday().toString() : "", false);
        o.println("      </div>");

        o.println("      <div class='form-group'>");
        o.println("        <label for='job'>Job</label>");
        o.println("        <input type='text' id='job' name='job' value='" + Html.esc(safe(cu.getJob())) + "'/>");
        o.println("      </div>");

        o.println("      <div class='section-title'>Address</div>");
        o.println("      <div class='form-grid'>");
        field(o, "Street", "street", "text", safe(addr.getStreet()), false);
        field(o, "City", "city", "text", safe(addr.getCity()), false);
        field(o, "State", "state", "text", safe(addr.getState()), false);
        field(o, "Zip", "zip", "text", safe(addr.getZip()), false);
        field(o, "Country", "country", "text", safe(addr.getCountry()), false);
        o.println("      </div>");

        o.println("      <div class='section-title'>Twilio Credentials</div>");
        o.println("      <div class='form-grid'>");
        field(o, "Account SID", "sid", "text", safe(cu.getSid()), false);
        field(o, "Auth Token", "token", "text", safe(cu.getToken()), false);
        o.println("      </div>");

        o.println("      <button type='submit' class='btn btn-save btn-full' style='margin-top:24px'>\uD83D\uDCBE Update Profile</button>");
        o.println("    </form>");
        o.println("  </div>");

        // ── Change Password ──
        o.println("  <div class='card anim-fadeup' style='max-width:700px;padding:28px;animation-delay:.08s'>");
        o.println("    <div class='section-title' style='border-top:none;margin-top:0;padding-top:0'>\uD83D\uDD12 Change Password</div>");
        o.println("    <form method='POST' action='" + ctx + "/customer/profile?action=password'>");
        o.println("      <div class='form-group'>");
        o.println("        <label for='new_password'>New Password</label>");
        o.println("        <input type='password' id='new_password' name='new_password' placeholder='Min. 6 characters' required minlength='6'/>");
        o.println("      </div>");
        o.println("      <button type='submit' class='btn btn-pass btn-full'>Change Password</button>");
        o.println("    </form>");
        o.println("  </div>");

        o.println("</div>");

        o.println(PageTemplate.footer());
        o.println("</body>");
        o.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        CustomerDAO dao = new CustomerDAO();
        String ctx = req.getContextPath();
        String action = req.getParameter("action");

        if ("password".equals(action)) {
            String newPass = req.getParameter("new_password");
            if (dao.updatePassword(cu.getCustomerId(), newPass)) {
                LogDAO.log(req.getRemoteAddr(), "PASSWD_CHANGE customer=" + cu.getCustomerId());
                resp.sendRedirect(ctx + "/customer/profile?success=Password+changed");
            } else {
                resp.sendRedirect(ctx + "/customer/profile?error=Password+change+failed");
            }
            return;
        }

        cu.setName(req.getParameter("name"));
        cu.setEmail(req.getParameter("email"));
        cu.setMsisdn(req.getParameter("msisdn"));
        String bday = req.getParameter("birthday");
        cu.setBirthday(bday != null && !bday.isEmpty() ? Date.valueOf(bday) : null);
        cu.setJob(req.getParameter("job"));

        CustomerAddress addr = new CustomerAddress(
                safe(req.getParameter("street")), safe(req.getParameter("city")),
                safe(req.getParameter("state")),  safe(req.getParameter("zip")),
                safe(req.getParameter("country")));
        cu.setCustomerAddress(addr);
        cu.setSid(req.getParameter("sid"));
        cu.setToken(req.getParameter("token"));

        if (dao.update(cu)) {
            req.getSession().setAttribute("customer", cu);
            LogDAO.log(req.getRemoteAddr(), "PROFILE_UPDATE customer=" + cu.getCustomerId());
            resp.sendRedirect(ctx + "/customer/profile?success=Profile+updated+successfully");
        } else {
            resp.sendRedirect(ctx + "/customer/profile?error=Update+failed");
        }
    }

    private static String safe(String s) { return s != null ? s : ""; }

    private void field(PrintWriter o, String label, String name, String type, String value, boolean required) {
        o.println("        <div class='form-group'>");
        o.println("          <label for='" + name + "'>" + label + "</label>");
        o.println("          <input type='" + type + "' id='" + name + "' name='" + name
                + "' value='" + Html.esc(value != null ? value : "") + "'" + (required ? " required" : "") + "/>");
        o.println("        </div>");
    }
}