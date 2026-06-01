package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;
import com.sms.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Date;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    // ── Zajel Icon (white, for the blue brand box) ──
    private static String brandIcon(String ctx) {
        return "<img src='" + ctx + "/logo.svg' alt='Zajel'"
                + " style='width:72px;height:72px;object-fit:contain;'/>";
    }

    // ═══════════════════════════════════════════════
    // GET — Show Register Page
    // ═══════════════════════════════════════════════
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String error = req.getParameter("error");
        String ctx = req.getContextPath();

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter o = resp.getWriter();

        o.println("<!DOCTYPE html>");
        o.println("<html lang='en'>");
        o.println("<head>");
        o.println("  <meta charset='UTF-8'/>");
        o.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
        o.println("  <title>Register — Zajel</title>");
        o.println("  <link rel='stylesheet' href='" + ctx + "/style.css'/>");
        o.println("  <style>");

        o.println("    body { padding: 40px 20px 60px; }");
        o.println("    .register-wrap { max-width: 800px; margin: 0 auto; animation: fadeUp .45s ease both; }");
        o.println("    @keyframes fadeUp { from { opacity:0; transform:translateY(16px); } to { opacity:1; transform:none; } }");

        o.println("    .reg-header { text-align: center; margin-bottom: 32px; }");
        o.println("    .reg-logo {");
        o.println("      display: inline-flex; align-items: center; justify-content: center;");
        o.println("      width: 56px; height: 56px; background: #1a73e8; border-radius: 18px;");
        o.println("      margin-bottom: 14px;");
        o.println("      box-shadow: 0 8px 24px rgba(26,115,232,.3);");
        o.println("    }");
        o.println("    .reg-header h1 { font-size: 22px; font-weight: 800; margin-bottom: 4px; }");
        o.println("    .reg-header p  { color: var(--muted); font-size: 13.5px; }");

        // Steps — blue theme
        o.println("    .steps { display: flex; align-items: center; justify-content: center; gap: 0; margin-bottom: 30px; }");
        o.println("    .step-dot {");
        o.println("      width: 36px; height: 36px; border-radius: 50%; border: 2px solid var(--border);");
        o.println("      display: flex; align-items: center; justify-content: center;");
        o.println("      font-size: 13px; font-weight: 700; color: var(--muted); background: var(--white);");
        o.println("      transition: all .3s; position: relative; z-index: 1;");
        o.println("    }");
        o.println("    .step-dot.active  { background: #1a73e8; border-color: #1a73e8; color: #fff; box-shadow: 0 4px 14px rgba(26,115,232,.4); }");
        o.println("    .step-dot.done    { background: #38a169; border-color: #38a169; color: #fff; }");
        o.println("    .step-label { font-size: 11px; font-weight: 600; color: var(--muted); margin-top: 6px; text-align: center; white-space: nowrap; }");
        o.println("    .step-label.active { color: #1a73e8; }");
        o.println("    .step-line { width: 80px; height: 2px; background: var(--border); border-radius: 99px; transition: background .3s; }");
        o.println("    .step-line.done { background: #38a169; }");
        o.println("    .step-wrap { display: flex; flex-direction: column; align-items: center; }");

        o.println("    .reg-card { background: var(--white); border: 1px solid var(--border); border-radius: 14px; box-shadow: 0 1px 3px rgba(0,0,0,.04); padding: 40px; }");

        o.println("    .step-panel { display: none; animation: fadeUp .35s ease both; }");
        o.println("    .step-panel.active { display: block; }");

        o.println("    .twilio-note {");
        o.println("      background: #eff6ff; border: 1px solid #bfdbfe;");
        o.println("      border-radius: 10px; padding: 14px 18px;");
        o.println("      font-size: 13px; color: #1e40af; margin-bottom: 22px; line-height: 1.7;");
        o.println("    }");
        o.println("    .twilio-note a { color: #1d4ed8; }");

        o.println("    .form-footer { text-align: center; margin-top: 22px; font-size: 13px; color: var(--muted); }");
        o.println("    .form-footer a { color: #1a73e8; text-decoration: none; font-weight: 600; }");

        o.println("    .step-actions { display: flex; gap: 12px; margin-top: 28px; }");
        o.println("    .step-actions .btn { flex: 1; padding: 13px; font-size: 14.5px; }");

        o.println("    .panel-title { font-size: 16px; font-weight: 800; margin-bottom: 4px; color: var(--text); }");
        o.println("    .panel-sub   { font-size: 13px; color: var(--muted); margin-bottom: 24px; }");

        o.println("    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 18px; }");
        o.println("    .form-grid .form-group { margin-bottom: 18px; }");
        o.println("    .form-grid .span-2 { grid-column: span 2; }");
        o.println("    @media (max-width: 600px) { .form-grid { grid-template-columns: 1fr; } .form-grid .span-2 { grid-column: span 1; } }");

        // Blue focus for register form
        o.println("    .reg-card .form-group input:focus,");
        o.println("    .reg-card .form-group select:focus {");
        o.println("      border-color: #1a73e8;");
        o.println("      box-shadow: 0 0 0 3px rgba(26,115,232,.12);");
        o.println("    }");

        o.println("  </style>");
        o.println("</head>");
        o.println("<body>");

        o.println("<div class='register-wrap'>");

        // ── Header with Zajel logo ───
        o.println("  <div class='reg-header'>");
        o.println("    <div class='reg-logo'>" + brandIcon(ctx) + "</div>");
        o.println("    <h1>Create your account</h1>");
        o.println("    <p>Two quick steps and you're ready to go</p>");
        o.println("  </div>");

        // ── Steps ───
        o.println("  <div class='steps'>");
        o.println("    <div class='step-wrap'>");
        o.println("      <div class='step-dot active' id='dot1'>1</div>");
        o.println("      <div class='step-label active' id='lbl1'>Your Info</div>");
        o.println("    </div>");
        o.println("    <div class='step-line' id='line1'></div>");
        o.println("    <div class='step-wrap'>");
        o.println("      <div class='step-dot' id='dot2'>2</div>");
        o.println("      <div class='step-label' id='lbl2'>Twilio Setup</div>");
        o.println("    </div>");
        o.println("  </div>");

        // ── Card ───
        o.println("  <div class='reg-card'>");

        if (error != null && !error.isEmpty()) {
            o.println("    <div class='alert alert-err'>\u26A0\uFE0F " + Html.esc(error) + "</div>");
        }

        o.println("    <form action='" + ctx + "/register' method='POST' novalidate id='regForm'>");

        // ── STEP 1: Personal Info ──
        o.println("      <div class='step-panel active' id='panel1'>");
        o.println("        <p class='panel-title'>Personal Information</p>");
        o.println("        <p class='panel-sub'>Tell us a bit about yourself to get started.</p>");
        o.println("        <div class='form-grid'>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='name'>Full Name *</label>");
        o.println("            <input type='text' id='name' name='name' placeholder='John Doe' required/>");
        o.println("          </div>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='email'>Email Address *</label>");
        o.println("            <input type='email' id='email' name='email' placeholder='john@example.com' required/>");
        o.println("          </div>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='birthday'>Birthday *</label>");
        o.println("            <input type='date' id='birthday' name='birthday' required/>");
        o.println("          </div>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='job'>Job Title</label>");
        o.println("            <input type='text' id='job' name='job' placeholder='Software Engineer'/>");
        o.println("          </div>");

        o.println("          <div class='form-group span-2'>");
        o.println("            <label for='address'>Address</label>");
        o.println("            <input type='text' id='address' name='address' placeholder='123 Main St, City'/>");
        o.println("          </div>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='password'>Password *</label>");
        o.println("            <input type='password' id='password' name='password' placeholder='Min. 8 characters' required autocomplete='new-password'/>");
        o.println("          </div>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='confirm'>Confirm Password *</label>");
        o.println("            <input type='password' id='confirm' name='confirm' placeholder='\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022' required autocomplete='new-password'/>");
        o.println("          </div>");

        o.println("        </div>"); // end form-grid

        o.println("        <div class='step-actions'>");
        o.println("          <button type='button' class='btn btn-primary' onclick='goStep2()'>");
        o.println("            Next \u2014 Twilio Setup \u2192");
        o.println("          </button>");
        o.println("        </div>");

        o.println("      </div>"); // end panel1

        // ── STEP 2: Twilio Credentials ──
        o.println("      <div class='step-panel' id='panel2'>");
        o.println("        <p class='panel-title'>Twilio Configuration</p>");
        o.println("        <p class='panel-sub'>Connect your Twilio account to start sending SMS.</p>");

        o.println("        <div class='twilio-note'>");
        o.println("          \uD83D\uDD12 Your credentials are stored securely and only used for sending SMS on your behalf.");
        o.println("          Find them in the <a href='https://console.twilio.com' target='_blank' rel='noopener'>Twilio Console</a>.");
        o.println("        </div>");

        o.println("        <div class='form-grid'>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='msisdn'>Phone Number (MSISDN) *</label>");
        o.println("            <input type='tel' id='msisdn' name='msisdn' placeholder='+1234567890' required/>");
        o.println("          </div>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='senderid'>Sender ID *</label>");
        o.println("            <input type='text' id='senderid' name='senderid' placeholder='MyBrand' required/>");
        o.println("          </div>");

        o.println("          <div class='form-group span-2'>");
        o.println("            <label for='sid'>Account SID *</label>");
        o.println("            <input type='text' id='sid' name='sid' placeholder='ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx' required style='font-family:monospace;font-size:13px'/>");
        o.println("          </div>");

        o.println("          <div class='form-group span-2'>");
        o.println("            <label for='token'>Auth Token *</label>");
        o.println("            <input type='password' id='token' name='token' placeholder='\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022' required style='font-family:monospace'/>");
        o.println("          </div>");

        o.println("        </div>"); // end form-grid

        o.println("        <div class='step-actions'>");
        o.println("          <button type='button' class='btn btn-outline' onclick='goStep1()' style='flex:.4'>");
        o.println("            \u2190 Back");
        o.println("          </button>");
        o.println("          <button type='submit' class='btn btn-primary'>");
        o.println("            Register &amp; Send Verification \u2713");
        o.println("          </button>");
        o.println("        </div>");

        o.println("      </div>"); // end panel2

        o.println("    </form>");

        o.println("    <div class='form-footer'>");
        o.println("      Already have an account? <a href='" + ctx + "/login'>Sign in</a>");
        o.println("    </div>");

        o.println("  </div>"); // end reg-card
        o.println("</div>"); // end register-wrap

        // ── JAVASCRIPT ──
        o.println("<script>");
        o.println("function goStep2() {");
        o.println("  var name     = document.getElementById('name');");
        o.println("  var email    = document.getElementById('email');");
        o.println("  var password = document.getElementById('password');");
        o.println("  var confirm  = document.getElementById('confirm');");
        o.println("  var birthday = document.getElementById('birthday');");
        o.println("");
        o.println("  if (!name.value.trim() || !email.value.trim() || !password.value || !birthday.value) {");
        o.println("    alert('Please fill in all required fields.'); return;");
        o.println("  }");
        o.println("  if (password.value !== confirm.value) {");
        o.println("    alert('Passwords do not match.'); return;");
        o.println("  }");
        o.println("  if (password.value.length < 8) {");
        o.println("    alert('Password must be at least 8 characters.'); return;");
        o.println("  }");
        o.println("");
        o.println("  document.getElementById('panel1').classList.remove('active');");
        o.println("  document.getElementById('panel2').classList.add('active');");
        o.println("");
        o.println("  document.getElementById('dot1').classList.remove('active');");
        o.println("  document.getElementById('dot1').classList.add('done');");
        o.println("  document.getElementById('dot1').textContent = '\\u2713';");
        o.println("  document.getElementById('lbl1').classList.remove('active');");
        o.println("");
        o.println("  document.getElementById('line1').classList.add('done');");
        o.println("");
        o.println("  document.getElementById('dot2').classList.add('active');");
        o.println("  document.getElementById('lbl2').classList.add('active');");
        o.println("}");
        o.println("");
        o.println("function goStep1() {");
        o.println("  document.getElementById('panel2').classList.remove('active');");
        o.println("  document.getElementById('panel1').classList.add('active');");
        o.println("");
        o.println("  document.getElementById('dot1').classList.add('active');");
        o.println("  document.getElementById('dot1').classList.remove('done');");
        o.println("  document.getElementById('dot1').textContent = '1';");
        o.println("  document.getElementById('lbl1').classList.add('active');");
        o.println("");
        o.println("  document.getElementById('line1').classList.remove('done');");
        o.println("");
        o.println("  document.getElementById('dot2').classList.remove('active');");
        o.println("  document.getElementById('lbl2').classList.remove('active');");
        o.println("}");
        o.println("</script>");

        o.println("</body>");
        o.println("</html>");
    }

    // ═══════════════════════════════════════════════
    // POST — Handle Registration
    // ═══════════════════════════════════════════════
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String ctx = req.getContextPath();
        CustomerDAO dao = new CustomerDAO();

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String birthday = req.getParameter("birthday");
        String job = req.getParameter("job");
        String address = req.getParameter("address");
        String msisdn = req.getParameter("msisdn");
        String senderid = req.getParameter("senderid");
        String sid = req.getParameter("sid");
        String token = req.getParameter("token");

        if (dao.findByEmail(email) != null) {
            resp.sendRedirect(ctx + "/register?error=Email+already+registered");
            return;
        }

        Customer cu = new Customer();
        cu.setName(name);
        cu.setEmail(email);
        cu.setPasswd(password);
        cu.setMsisdn(msisdn);

        if (birthday != null && !birthday.isEmpty()) {
            cu.setBirthday(Date.valueOf(birthday));
        }
        cu.setJob(job);

        CustomerAddress addr = new CustomerAddress();
        if (address != null && !address.isEmpty()) {
            addr.setStreet(address);
        }
        cu.setCustomerAddress(addr);

        cu.setSid(sid);
        cu.setToken(token);

        String code = TwilioHelper.generateCode();
        cu.setVerificationCode(code);

        if (!dao.register(cu)) {
            resp.sendRedirect(ctx + "/register?error=Registration+failed.+Please+try+again.");
            return;
        }

        LogDAO.log(req.getRemoteAddr(), "REGISTER: " + email);

        try {
            TwilioHelper.sendSms(sid, token, senderid, msisdn,
                    "Welcome to Zajel! Your verification code is: " + code);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(ctx + "/register?error=Registered+but+SMS+failed:+"
                    + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
            return;
        }

        HttpSession s = req.getSession(true);
        s.setAttribute("unverified_id", cu.getCustomerId());
        s.setAttribute("verify_code", code);

        resp.sendRedirect(ctx + "/verify");
    }
}
