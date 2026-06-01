package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;
import com.sms.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.URLEncoder;

@WebServlet("/customer/send")
public class SendSmsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String ctx = req.getContextPath();
        String error = req.getParameter("error");
        String success = req.getParameter("success");
        String senderid = cu.getMsisdn();

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter o = resp.getWriter();

        o.println("<!DOCTYPE html>");
        o.println("<html lang='en'>");
        o.println("<head>");
        o.println("  <meta charset='UTF-8'/>");
        o.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
        o.println("  <title>Send SMS — Zajel</title>");
        o.println("  <link rel='stylesheet' href='" + ctx + "/style.css'/>");
        o.println("  <style>");
        o.println(PageTemplate.headerFooterCSS());

        o.println("    .send-layout { display:flex; gap:56px; align-items:flex-start; max-width:1500px; margin:0 auto; }");
        o.println("    .send-card { flex:1.2; min-width:0; }");

        // Phone — blue theme
        o.println("    .phone-wrap { width:340px; flex-shrink:0; display:flex; flex-direction:column; align-items:center; gap:16px; position:sticky; top:90px; }");
        o.println("    .phone-label { font-size:13px; font-weight:700; text-transform:uppercase; letter-spacing:.07em; color:var(--muted); }");
        o.println("    .phone { width:340px; background:#1a1a2e; border-radius:48px; padding:22px 18px 28px;");
        o.println("      box-shadow:0 20px 60px rgba(0,0,0,.25),0 0 0 1px rgba(255,255,255,.06); position:relative; }");

        o.println("    .phone-notch { width:96px; height:26px; background:#111; border-radius:0 0 18px 18px;");
        o.println("      margin:0 auto 16px; display:flex; align-items:center; justify-content:center; gap:8px; }");
        o.println("    .phone-notch .cam { width:10px; height:10px; border-radius:50%; background:#2a2a3e; border:1px solid #333; }");

        o.println("    .phone-screen { background:#f0f0f5; border-radius:30px; overflow:hidden; min-height:440px; display:flex; flex-direction:column; }");
        o.println("    .phone-screen-header { background:#fff; padding:14px 20px 12px; border-bottom:1px solid #e8e8ec; text-align:center; }");
        o.println("    .phone-screen-header .to-num { font-size:15px; font-weight:700; color:#1a1a2e; }");
        o.println("    .phone-screen-header .sub-txt { font-size:12px; color:#999; margin-top:2px; }");

        // Bubbles — blue instead of red
        o.println("    .phone-messages { flex:1; padding:16px 14px; display:flex; flex-direction:column; gap:10px; overflow:hidden; }");
        o.println("    .phone-bubble { max-width:80%; padding:10px 14px; border-radius:18px; font-size:13px; line-height:1.45; color:#1a1a2e; word-break:break-word; }");
        o.println("    .phone-bubble.out { background:#1a73e8; color:#fff; align-self:flex-end; border-bottom-right-radius:4px; }");
        o.println("    .phone-bubble.placeholder { background:#e2e2ea; align-self:flex-end; border-bottom-right-radius:4px; color:#999; font-style:italic; }");
        o.println("    .phone-bubble.in { background:#fff; align-self:flex-start; border-bottom-left-radius:4px; box-shadow:0 1px 3px rgba(0,0,0,.08); }");
        o.println("    .bubble-time { font-size:10px; color:rgba(255,255,255,.6); margin-top:3px; text-align:right; }");
        o.println("    .bubble-time.dark { color:#bbb; }");

        // Send button — blue
        o.println("    .phone-input-bar { background:#fff; border-top:1px solid #e8e8ec; padding:12px 14px; display:flex; align-items:center; gap:8px; }");
        o.println("    .phone-input-bar .fake-input { flex:1; background:#f0f0f5; border-radius:99px; padding:8px 14px; font-size:12px; color:#bbb; }");
        o.println("    .phone-send-btn { width:36px; height:36px; border-radius:50%; background:#1a73e8; display:flex; align-items:center; justify-content:center; font-size:15px; flex-shrink:0; color:#fff; }");
        o.println("    .phone-home-bar { width:80px; height:5px; background:rgba(255,255,255,.2); border-radius:99px; margin:14px auto 0; }");

        o.println("    @media (max-width:700px) { .send-layout { flex-direction:column; } .phone-wrap { display:none; } }");
        o.println("  </style>");
        o.println("</head>");
        o.println("<body class='page-shell'>");

        o.println(PageTemplate.customerHeader(ctx, "Send SMS"));

        o.println("<div class='page-body'>");
        o.println("  <h1 class='page-title'>Send Message</h1>");
        o.println("  <p class='page-sub'>Compose and send an SMS using your Twilio credentials.</p>");

        if (error != null && !error.isEmpty())
            o.println("  <div class='alert alert-err'>\u26A0\uFE0F " + Html.esc(error) + "</div>");
        if (success != null && !success.isEmpty())
            o.println("  <div class='alert alert-ok'>\u2705 " + Html.esc(success) + "</div>");

        o.println("  <div class='send-layout'>");

        // Form
        o.println("    <div class='send-card'>");
        o.println("      <article class='card' style='padding:36px'>");
        o.println("        <form action='" + ctx + "/customer/send' method='POST' novalidate>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='from'>From (Sender ID)</label>");
        o.println("            <input type='text' id='from' name='from' placeholder='Your allowed Sender ID' required");
        o.println("                   value='" + Html.esc(senderid) + "' oninput='updatePreview()'/>");
        o.println("          </div>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='to'>To (Recipient Number)</label>");
        o.println("            <input type='tel' id='to' name='to' placeholder='+1234567890' required oninput='updatePreview()'/>");
        o.println("          </div>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='body'>Message Body</label>");
        o.println("            <textarea id='body' name='body' maxlength='160'");
        o.println("                      placeholder='Type your message here\u2026' required");
        o.println("                      oninput='updateCount(this); updatePreview()'></textarea>");
        o.println("            <div style='display:flex;justify-content:space-between;margin-top:5px'>");
        o.println("              <span style='font-size:11.5px;color:var(--muted)'>Max 160 characters</span>");
        o.println("              <span id='charCount' style='font-size:11.5px;color:var(--muted);font-family:monospace'>0 / 160</span>");
        o.println("            </div>");
        o.println("          </div>");

        o.println("          <button type='submit' class='btn btn-primary btn-full'>Send SMS \u2709</button>");
        o.println("        </form>");

        o.println("        <p style='font-size:12px;color:var(--muted);text-align:center;margin-top:18px;line-height:1.6'>");
        o.println("          This will securely use your pre-configured Account SID and Token.");
        o.println("        </p>");
        o.println("      </article>");
        o.println("    </div>");

        // Phone mockup
        o.println("    <div class='phone-wrap'>");
        o.println("      <span class='phone-label'>Preview</span>");
        o.println("      <div class='phone'>");
        o.println("        <div class='phone-notch'><div class='cam'></div></div>");
        o.println("        <div class='phone-screen'>");
        o.println("          <div class='phone-screen-header'>");
        o.println("            <div class='to-num' id='prev-to'>+1234567890</div>");
        o.println("            <div class='sub-txt'>via " + Html.esc(senderid) + "</div>");
        o.println("          </div>");
        o.println("          <div class='phone-messages' id='prev-messages'>");
        o.println("            <div class='phone-bubble in'>");
        o.println("              Hey, do you have any updates?");
        o.println("              <div class='bubble-time dark'>10:14 AM</div>");
        o.println("            </div>");
        o.println("            <div class='phone-bubble placeholder' id='prev-bubble'>");
        o.println("              Your message will appear here\u2026");
        o.println("            </div>");
        o.println("          </div>");
        o.println("          <div class='phone-input-bar'>");
        o.println("            <div class='fake-input'>Message</div>");
        o.println("            <div class='phone-send-btn'>\u2191</div>");
        o.println("          </div>");
        o.println("        </div>");
        o.println("        <div class='phone-home-bar'></div>");
        o.println("      </div>");
        o.println("    </div>");

        o.println("  </div>");
        o.println("</div>");

        o.println(PageTemplate.footer());

        o.println("<script>");
        o.println("function updateCount(ta){document.getElementById('charCount').textContent=ta.value.length+' / 160'}");
        o.println("function updatePreview(){");
        o.println("  var toVal=document.getElementById('to').value.trim()||'+1234567890';");
        o.println("  var bodyVal=document.getElementById('body').value.trim();");
        o.println("  document.getElementById('prev-to').textContent=toVal;");
        o.println("  var bubble=document.getElementById('prev-bubble');");
        o.println("  if(bodyVal){bubble.className='phone-bubble out';bubble.innerHTML=escH(bodyVal)+'<div class=\"bubble-time\">Now</div>'}");
        o.println("  else{bubble.className='phone-bubble placeholder';bubble.innerHTML='Your message will appear here\\u2026'}");
        o.println("}");
        o.println("function escH(s){var d=document.createElement('div');d.appendChild(document.createTextNode(s));return d.innerHTML}");
        o.println("</script>");

        o.println("</body>");
        o.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String body = req.getParameter("body");
        String ctx = req.getContextPath();

        if (from == null || from.trim().isEmpty()) from = cu.getMsisdn();

        ErrCodDAO errDao = new ErrCodDAO();
        MsgDAO msgDao = new MsgDAO();

        Msg msg = new Msg();
        msg.setSenderId(cu.getCustomerId());
        msg.setReceiverMsisdn(to);
        msg.setMsgBody(body);

        try {
            TwilioHelper.sendSms(cu.getSid(), cu.getToken(), from, to, body);
            ErrCod ok = errDao.findByMsg("SUCCESS");
            msg.setErrorCode(ok != null ? ok.getErrorId() : null);
            msg.setStatus("SUCCESS");
            msgDao.save(msg);
            LogDAO.log(req.getRemoteAddr(), "SMS_SENT by=" + cu.getCustomerId() + " from=" + from + " to=" + to);
            resp.sendRedirect(ctx + "/customer/send?success=SMS+sent+successfully!");

        } catch (com.twilio.exception.AuthenticationException e) {
            ErrCod err = errDao.findByMsg("AUTH_FAILED");
            msg.setErrorCode(err != null ? err.getErrorId() : null);
            msg.setStatus("FAILED");
            msgDao.save(msg);
            LogDAO.log(req.getRemoteAddr(), "SMS_FAIL auth by=" + cu.getCustomerId());
            resp.sendRedirect(ctx + "/customer/send?error=Authentication+failed.+Check+Twilio+credentials.");

        } catch (com.twilio.exception.ApiException e) {
            ErrCod err = errDao.findByMsg("INVALID_NUMBER");
            msg.setErrorCode(err != null ? err.getErrorId() : null);
            msgDao.save(msg);
            LogDAO.log(req.getRemoteAddr(), "SMS_FAIL api by=" + cu.getCustomerId());
            resp.sendRedirect(ctx + "/customer/send?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));

        } catch (Exception e) {
            ErrCod err = errDao.findByMsg("UNKNOWN_ERROR");
            msg.setErrorCode(err != null ? err.getErrorId() : null);
            msgDao.save(msg);
            LogDAO.log(req.getRemoteAddr(), "SMS_FAIL by=" + cu.getCustomerId() + " : " + e.getMessage());
            resp.sendRedirect(ctx + "/customer/send?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}