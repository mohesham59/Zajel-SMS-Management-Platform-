package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;
import com.sms.util.Html;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

// ── Zajel Logo — actual brand image ──
// LOGO_WHITE   : used in left panel heading area (white label on dark bg)
// LOGO_ICON_WHITE : used inside the blue brand-icon box
// Both reference logo.svg saved in webapp/
    private static String logoWhite(String ctx) {
        return "<img src='" + ctx + "/logo.svg' alt='Zajel'"
                + " style='height:48px;width:48px;object-fit:contain;filter:brightness(0) invert(1)'/>"
                + "<span style='font-size:24px;font-weight:800;color:#fff;"
                + "letter-spacing:-.5px;margin-left:10px;vertical-align:middle'>Zajel</span>";
    }

    private static String logoIconWhite(String ctx) {
        return "<img src='" + ctx + "/logo.svg' alt='Zajel'"
                + " style='width:72px;height:72px;object-fit:contain;'/>";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Already logged in? Redirect
        HttpSession s = req.getSession(false);
        if (s != null) {
            if (s.getAttribute("admin") != null) {
                resp.sendRedirect(req.getContextPath() + "/admin/home");
                return;
            }
            if (s.getAttribute("customer") != null) {
                resp.sendRedirect(req.getContextPath() + "/customer/home");
                return;
            }
        }

        String error = req.getParameter("error");
        String success = req.getParameter("success");
        String ctx = req.getContextPath();

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter o = resp.getWriter();

        o.println("<!DOCTYPE html>");
        o.println("<html lang='en'>");
        o.println("<head>");
        o.println("  <meta charset='UTF-8'/>");
        o.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
        o.println("  <title>Login — Zajel</title>");
        o.println("  <link rel='stylesheet' href='" + ctx + "/style.css'/>");
        o.println("  <style>");
        o.println("    body { display:flex; height:100vh; overflow:hidden; background:var(--navy); }");

        // ── Left Panel ──
        o.println("    .auth-left {");
        o.println("      flex:1; position:relative; overflow:hidden;");
        o.println("      display:flex; align-items:center; justify-content:center;");
        o.println("      background:radial-gradient(ellipse at 35% 50%, #0d2137 0%, #060e1a 65%);");
        o.println("    }");

        // Bubbles
        o.println("    .bubble-layer { position:absolute; inset:0; pointer-events:none; }");
        o.println("    .bubble {");
        o.println("      position:absolute; bottom:-80px; padding:9px 16px;");
        o.println("      font-size:12px; font-weight:600; white-space:nowrap;");
        o.println("      animation:floatUp linear infinite; opacity:0;");
        o.println("    }");
        o.println("    .bubble.out { background:rgba(26,115,232,.18); border:1px solid rgba(26,115,232,.35); color:#93c5fd; border-radius:16px 16px 3px 16px; }");
        o.println("    .bubble.in  { background:rgba(255,255,255,.06); border:1px solid rgba(255,255,255,.12); color:#a0aec0; border-radius:16px 16px 16px 3px; }");
        o.println("    @keyframes floatUp {");
        o.println("      0%   { transform:translateY(0); opacity:0; }");
        o.println("      8%   { opacity:.85; }");
        o.println("      85%  { opacity:.85; }");
        o.println("      100% { transform:translateY(-110vh); opacity:0; }");
        o.println("    }");

        // Rings
        o.println("    .ring {");
        o.println("      position:absolute; border-radius:50%;");
        o.println("      border:1px solid rgba(26,115,232,.08);");
        o.println("      top:50%; left:50%; transform:translate(-50%,-50%);");
        o.println("      animation:rpulse 3.5s ease-out infinite;");
        o.println("    }");
        o.println("    .ring:nth-child(2) { animation-delay:1.15s; }");
        o.println("    .ring:nth-child(3) { animation-delay:2.3s; }");
        o.println("    @keyframes rpulse {");
        o.println("      0%   { width:80px;  height:80px;  opacity:.7; }");
        o.println("      100% { width:600px; height:600px; opacity:0;  }");
        o.println("    }");

        // Brand
        o.println("    .brand { text-align:center; z-index:1; padding:0 48px; }");
        o.println("    .brand-icon {");
        o.println("      width:72px; height:72px; margin:0 auto 24px;");
        o.println("      background:var(--blue, #1a73e8); border-radius:22px;");
        o.println("      display:flex; align-items:center; justify-content:center;");
        o.println("      box-shadow:0 12px 40px rgba(26,115,232,.45);");
        o.println("      animation:fadeUp .5s .1s ease both;");
        o.println("    }");
        o.println("    .brand h1 { font-size:32px; font-weight:800; color:#fff; margin-bottom:14px; letter-spacing:-.5px; animation:fadeUp .5s .2s ease both; }");
        o.println("    .brand p  { font-size:15px; color:#718096; max-width:300px; margin:0 auto 36px; line-height:1.8; animation:fadeUp .5s .3s ease both; }");
        o.println("    .brand-stats { display:flex; gap:40px; justify-content:center; animation:fadeUp .5s .4s ease both; }");
        o.println("    .bstat-val { font-size:22px; font-weight:800; color:#1a73e8; }");
        o.println("    .bstat-lbl { font-size:11px; color:#4a5568; margin-top:2px; }");

        // ── Right Panel ──
        o.println("    .auth-right {");
        o.println("      width:580px; flex-shrink:0; background:var(--white);");
        o.println("      display:flex; align-items:center; justify-content:center;");
        o.println("      padding:60px 68px; overflow-y:auto;");
        o.println("    }");
        o.println("    .login-form { width:100%; animation:slideIn .45s .1s ease both; }");
        o.println("    .login-form h2  { font-size:30px; font-weight:800; margin-bottom:8px; }");
        o.println("    .login-form .sub { color:var(--muted); font-size:15px; margin-bottom:38px; }");
        o.println("    .login-form .form-group { margin-bottom:22px; }");
        o.println("    .login-form .form-group label { font-size:12.5px; margin-bottom:9px; }");
        o.println("    .login-form .form-group input,");
        o.println("    .login-form .form-group select { padding:14px 16px; font-size:15px; }");
        o.println("    .login-form .form-group input:focus,");
        o.println("    .login-form .form-group select:focus {");
        o.println("      border-color:#1a73e8;");
        o.println("      box-shadow:0 0 0 3px rgba(26,115,232,.12);");
        o.println("    }");
        o.println("    .login-form .btn { padding:15px; font-size:15px; }");
        o.println("    .divider-or {");
        o.println("      text-align:center; color:var(--muted); font-size:13.5px;");
        o.println("      position:relative; margin:26px 0;");
        o.println("    }");
        o.println("    .divider-or::before, .divider-or::after {");
        o.println("      content:''; position:absolute; top:50%;");
        o.println("      width:36%; height:1px; background:var(--border);");
        o.println("    }");
        o.println("    .divider-or::before { left:0; }");
        o.println("    .divider-or::after  { right:0; }");

        // Login button overrides (blue)
        o.println("    .login-btn-primary {");
        o.println("      background:#1a73e8 !important; color:#fff;");
        o.println("      box-shadow:0 4px 14px rgba(26,115,232,.35);");
        o.println("    }");
        o.println("    .login-btn-primary:hover { background:#0d47a1 !important; }");
        o.println("    .login-btn-outline {");
        o.println("      background:transparent; color:#1a73e8;");
        o.println("      border:1.5px solid #1a73e8;");
        o.println("    }");
        o.println("    .login-btn-outline:hover { background:rgba(26,115,232,.06); }");

        o.println("    @keyframes fadeUp  { from { opacity:0; transform:translateY(16px); } to { opacity:1; transform:none; } }");
        o.println("    @keyframes slideIn { from { opacity:0; transform:translateX(20px); } to { opacity:1; transform:none; } }");
        o.println("    @media (max-width:980px) { .auth-left { display:none; } .auth-right { width:100%; padding:48px 36px; } }");
        o.println("  </style>");
        o.println("</head>");
        o.println("<body>");

        // ── LEFT PANEL ──
        o.println("<div class='auth-left'>");
        o.println("  <div class='ring'></div><div class='ring'></div><div class='ring'></div>");
        o.println("  <div class='bubble-layer' id='bubbleLayer'></div>");
        o.println("  <div class='brand'>");
        o.println("    <div class='brand-icon'>" + logoIconWhite(ctx) + "</div>");
        o.println("    <h1>Zajel</h1>");
        o.println("    <p>Send, track, and manage SMS messages reliably and at scale.</p>");
        o.println("    <div class='brand-stats'>");
        o.println("      <div><div class='bstat-val'>99.9%</div><div class='bstat-lbl'>Uptime</div></div>");
        o.println("      <div><div class='bstat-val'>&lt;2s</div><div class='bstat-lbl'>Delivery</div></div>");
        o.println("      <div><div class='bstat-val'>Global</div><div class='bstat-lbl'>Reach</div></div>");
        o.println("    </div>");
        o.println("  </div>");
        o.println("</div>");

        // ── RIGHT PANEL ──
        o.println("<div class='auth-right'>");
        o.println("  <div class='login-form'>");
        o.println("    <h2>Welcome back \uD83D\uDC4B</h2>");
        o.println("    <p class='sub'>Sign in to your SMS account to continue</p>");

        if (error != null && !error.isEmpty()) {
            o.println("    <div class='alert alert-err'>\u26A0\uFE0F " + Html.esc(error) + "</div>");
        }
        if (success != null && !success.isEmpty()) {
            o.println("    <div class='alert alert-ok'>\u2705 " + Html.esc(success) + "</div>");
        }

        o.println("    <form action='" + ctx + "/login' method='POST'>");

        o.println("      <div class='form-group'>");
        o.println("        <label for='email'>Email Address</label>");
        o.println("        <input type='email' id='email' name='email' placeholder='name@company.com' required autocomplete='email'/>");
        o.println("      </div>");

        o.println("      <div class='form-group'>");
        o.println("        <label for='password'>Password</label>");
        o.println("        <input type='password' id='password' name='password' placeholder='••••••••' required autocomplete='current-password'/>");
        o.println("      </div>");

        o.println("      <div class='form-group' style='margin-bottom:32px'>");
        o.println("        <label for='role'>Sign in as</label>");
        o.println("        <select id='role' name='role'>");
        o.println("          <option value='customer'>Customer</option>");
        o.println("          <option value='admin'>Administrator</option>");
        o.println("        </select>");
        o.println("      </div>");

        o.println("      <button type='submit' class='btn login-btn-primary btn-full'>Sign In</button>");
        o.println("    </form>");

        o.println("    <div class='divider-or'> Don't have an account? </div>");
        o.println("    <a href='" + ctx + "/register' class='btn login-btn-outline btn-full' style='text-decoration:none'>Create an account</a>");

        o.println("  </div>");
        o.println("</div>");

        // ── BUBBLE JS ──
        o.println("<script>");
        o.println("var MSGS=['\\uD83D\\uDCE6 Order shipped!','\\uD83D\\uDD14 OTP: 748291','\\u2705 Payment confirmed',");
        o.println("  '\\uD83D\\uDCF1 Meeting at 3 PM','\\uD83D\\uDE80 Welcome aboard!','\\uD83D\\uDCAC New message',");
        o.println("  '\\uD83D\\uDD10 Login alert','\\uD83D\\uDCE9 Code: 391027','\\uD83D\\uDC4B Hello there!'];");
        o.println("function spawn(){");
        o.println("  var el=document.createElement('div');");
        o.println("  el.className='bubble '+(Math.random()>.5?'out':'in');");
        o.println("  el.textContent=MSGS[Math.floor(Math.random()*MSGS.length)];");
        o.println("  var dur=6+Math.random()*5, delay=Math.random()*1.5;");
        o.println("  el.style.cssText='left:'+(4+Math.random()*88)+'%;animation-duration:'+dur+'s;animation-delay:'+delay+'s';");
        o.println("  document.getElementById('bubbleLayer').appendChild(el);");
        o.println("  setTimeout(function(){el.remove()},(dur+delay+.5)*1000);");
        o.println("}");
        o.println("setInterval(spawn,1100);");
        o.println("for(var i=0;i<5;i++) setTimeout(spawn,i*280);");
        o.println("</script>");

        o.println("</body>");
        o.println("</html>");
    }

    // ═══════════════════════════════════════════════
    // POST — Handle login
    // ═══════════════════════════════════════════════
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String pass = req.getParameter("password");
        String role = req.getParameter("role");
        String ctx = req.getContextPath();

        LogDAO.log(req.getRemoteAddr(), "LOGIN attempt: " + email + " role=" + role);

        if ("admin".equals(role)) {
            Admin admin = new AdminDAO().authenticate(email, pass);
            if (admin == null) {
                resp.sendRedirect(ctx + "/login?error=Invalid+admin+credentials");
                return;
            }
            HttpSession s = req.getSession(true);
            s.setAttribute("admin", admin);
            s.setMaxInactiveInterval(30 * 60);
            resp.sendRedirect(ctx + "/admin/home");

        } else {
            Customer cu = new CustomerDAO().authenticate(email, pass);
            if (cu == null) {
                resp.sendRedirect(ctx + "/login?error=Invalid+email+or+password");
                return;
            }
            if (!cu.isVerified()) {
                HttpSession s = req.getSession(true);
                s.setAttribute("unverified_id", cu.getCustomerId());
                resp.sendRedirect(ctx + "/verify");
                return;
            }
            HttpSession s = req.getSession(true);
            s.setAttribute("customer", cu);
            s.setMaxInactiveInterval(30 * 60);
            resp.sendRedirect(ctx + "/customer/home");
        }
    }
}
