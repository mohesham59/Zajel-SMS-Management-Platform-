package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;
import com.sms.util.Html;
import com.sms.util.PageTemplate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Date;

@WebServlet("/admin/customer-edit")
public class AdminCustomerEditServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Admin admin = (Admin) req.getSession().getAttribute("admin");
        if (admin == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String ctx = req.getContextPath();
        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (Exception e) {
            resp.sendRedirect(ctx + "/admin/customers?success=Invalid+customer+ID");
            return;
        }

        Customer cu = new CustomerDAO().findById(id);
        if (cu == null) {
            resp.sendRedirect(ctx + "/admin/customers?success=Customer+not+found");
            return;
        }

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
        o.println("  <title>Edit Customer — Zajel Admin</title>");
        o.println("  <link rel='stylesheet' href='" + ctx + "/style.css'/>");
        o.println("  <style>");
        o.println(PageTemplate.headerFooterCSS());

        // Purple-themed form
        o.println("    .form-grid { display:grid; grid-template-columns:1fr 1fr; gap:0 20px; }");
        o.println("    @media(max-width:600px) { .form-grid { grid-template-columns:1fr; } }");
        o.println("    .section-title { font-size:14px; font-weight:700; color:#2d3748; margin:20px 0 12px; padding-top:16px; border-top:1px solid #e2e8f0; }");
        o.println("    .section-title:first-of-type { border-top:none; margin-top:0; padding-top:0; }");

        // Purple focus
        o.println("    .form-group input:focus, .form-group select:focus, .form-group textarea:focus {");
        o.println("      border-color:#7c4dff !important;");
        o.println("      box-shadow:0 0 0 3px rgba(124,77,255,.12) !important;");
        o.println("    }");

        // Purple buttons
        o.println("    .btn-save { background:#7c4dff; color:#fff; box-shadow:0 4px 14px rgba(124,77,255,.35); }");
        o.println("    .btn-save:hover { background:#6200ea; }");
        o.println("    .btn-back { background:#f1f5f9; color:#2d3748; border:1.5px solid #e2e8f0; }");
        o.println("    .btn-back:hover { background:#e2e8f0; }");

        o.println("  </style>");
        o.println("</head>");
        o.println("<body class='page-shell admin-page'>");

        o.println(PageTemplate.adminHeader(ctx, "Manage Customers"));

        o.println("<div class='page-body'>");

        o.println("  <div style='margin-bottom:28px'>");
        o.println("    <h1 class='page-title'>\u270F\uFE0F Edit Customer #" + cu.getCustomerId() + "</h1>");
        o.println("    <p style='color:#718096;font-size:13.5px'>Update customer information and Twilio credentials.</p>");
        o.println("  </div>");

        if (error != null && !error.isEmpty()) {
            o.println("  <div class='alert alert-err' style='margin-bottom:20px'>\u26A0\uFE0F " + Html.esc(error) + "</div>");
        }
        if (success != null && !success.isEmpty()) {
            o.println("  <div class='alert alert-ok' style='margin-bottom:20px'>\u2705 " + Html.esc(success) + "</div>");
        }

        // ═══ IMPORTANT: explicit action attribute ═══
        o.println("  <div class='card anim-fadeup' style='max-width:700px;padding:28px'>");
        o.println("    <form method='POST' action='" + ctx + "/admin/customer-edit'>");
        o.println("      <input type='hidden' name='id' value='" + cu.getCustomerId() + "'/>");

        // Personal Info
        o.println("      <div class='section-title'>Personal Information</div>");
        o.println("      <div class='form-grid'>");
        formField(o, "Name", "name", "text", cu.getName(), true);
        formField(o, "Email", "email", "email", cu.getEmail(), true);
        formField(o, "MSISDN", "msisdn", "tel", cu.getMsisdn(), true);
        formField(o, "Birthday", "birthday", "date",
                cu.getBirthday() != null ? cu.getBirthday().toString() : "", false);
        o.println("      </div>");

        o.println("      <div class='form-group' style='margin-bottom:0'>");
        o.println("        <label for='job'>Job</label>");
        o.println("        <input type='text' id='job' name='job' value='" + Html.esc(safe(cu.getJob())) + "'/>");
        o.println("      </div>");

        // Address
        o.println("      <div class='section-title'>Address</div>");
        o.println("      <div class='form-grid'>");
        formField(o, "Street", "street", "text", safe(addr.getStreet()), false);
        formField(o, "City", "city", "text", safe(addr.getCity()), false);
        formField(o, "State", "state", "text", safe(addr.getState()), false);
        formField(o, "Zip", "zip", "text", safe(addr.getZip()), false);
        formField(o, "Country", "country", "text", safe(addr.getCountry()), false);
        o.println("      </div>");

        // Twilio
        o.println("      <div class='section-title'>Twilio Credentials</div>");
        o.println("      <div class='form-grid'>");
        formField(o, "Account SID", "sid", "text", safe(cu.getSid()), false);
        formField(o, "Auth Token", "token", "text", safe(cu.getToken()), false);
        o.println("      </div>");

        // Buttons
        o.println("      <div style='display:flex;gap:10px;margin-top:24px'>");
        o.println("        <button type='submit' class='btn btn-save'>\uD83D\uDCBE Save Changes</button>");
        o.println("        <a href='" + ctx + "/admin/customers' class='btn btn-back' style='text-decoration:none'>Cancel</a>");
        o.println("      </div>");

        o.println("    </form>");
        o.println("  </div>");
        o.println("</div>");

        o.println(PageTemplate.footer());
        o.println("</body>");
        o.println("</html>");
    }

    // ═══════════════════════════════════════════════
    // POST — Handle Edit (FIXED with full error detail)
    // ═══════════════════════════════════════════════
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String ctx = req.getContextPath();

        // ── Parse ID safely ──
        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (Exception e) {
            resp.sendRedirect(ctx + "/admin/customers?success=Invalid+customer+ID");
            return;
        }

        CustomerDAO dao = new CustomerDAO();
        Customer cu = dao.findById(id);
        if (cu == null) {
            resp.sendRedirect(ctx + "/admin/customers?success=Customer+not+found");
            return;
        }

        try {
            // ── Update fields from form (preserve password!) ──
            cu.setName(req.getParameter("name"));
            cu.setEmail(req.getParameter("email"));
            cu.setMsisdn(req.getParameter("msisdn"));

            // ── Birthday — parse safely ──
            String bday = req.getParameter("birthday");
            if (bday != null && !bday.trim().isEmpty()) {
                try {
                    cu.setBirthday(Date.valueOf(bday.trim()));
                } catch (IllegalArgumentException e) {
                    System.err.println("[AdminEdit] Bad date format: " + bday);
                    cu.setBirthday(null);
                }
            } else {
                cu.setBirthday(null);
            }

            cu.setJob(req.getParameter("job"));

            // ── Build address safely (never pass null to constructor) ──
            CustomerAddress addr = new CustomerAddress(
                safe(req.getParameter("street")),
                safe(req.getParameter("city")),
                safe(req.getParameter("state")),
                safe(req.getParameter("zip")),
                safe(req.getParameter("country"))
            );
            cu.setCustomerAddress(addr);

            // Debug: log the literal being sent to PostgreSQL
            System.out.println("[AdminEdit] Address literal: " + addr.toPostgresLiteral());

            cu.setSid(req.getParameter("sid"));
            cu.setToken(req.getParameter("token"));

            // ── Attempt update ──
            if (dao.update(cu)) {
                LogDAO.log(req.getRemoteAddr(), "ADMIN_EDIT customer=" + id);
                resp.sendRedirect(ctx + "/admin/customer-edit?id=" + id
                        + "&success=Customer+updated+successfully");
            } else {
                resp.sendRedirect(ctx + "/admin/customer-edit?id=" + id
                        + "&error=Update+failed.+Check+server+logs+for+SQL+details.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            String errMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            resp.sendRedirect(ctx + "/admin/customer-edit?id=" + id
                    + "&error=" + java.net.URLEncoder.encode("Error: " + errMsg, "UTF-8"));
        }
    }

    // ── Null-safe helper ──
    private static String safe(String s) {
        return s != null ? s : "";
    }

    private void formField(PrintWriter o, String label, String name, String type,
                           String value, boolean required) {
        o.println("        <div class='form-group'>");
        o.println("          <label for='" + name + "'>" + label + "</label>");
        o.println("          <input type='" + type + "' id='" + name + "' name='" + name
                + "' value='" + Html.esc(value != null ? value : "") + "'"
                + (required ? " required" : "") + "/>");
        o.println("        </div>");
    }
}