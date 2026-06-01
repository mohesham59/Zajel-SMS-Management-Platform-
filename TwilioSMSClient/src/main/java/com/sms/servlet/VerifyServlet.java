/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.Customer;
import com.sms.util.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;;

/**
 *
 * @author mohesham
 */


@WebServlet("/verify")
public class VerifyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("unverified_id") == null) {
            resp.sendRedirect(req.getContextPath()+"/login");
            return;
        }
        String error = req.getParameter("error");
        String info  = req.getParameter("info");

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter o = resp.getWriter();
        o.println(Html.top("Verify"));
        o.println("<div class='center-box'><div class='card login-card'>");
        o.println("<h2 style='text-align:center'>🔐 Verify Account</h2>");
        o.println("<p style='text-align:center;color:#666;margin-bottom:18px'>Enter the 6-digit code sent to your phone</p>");
        if (error != null) o.println(Html.alert("err", error));
        if (info  != null) o.println(Html.alert("ok",  info));

        o.println("<form method='POST'>");
        o.println("<label>Verification Code</label>");
        o.println("<input type='text' name='code' required maxlength='6' "
                + "style='text-align:center;font-size:24px;letter-spacing:8px' placeholder='------'>");
        o.println("<button class='btn' style='width:100%'>Verify</button>");
        o.println("</form>");

        o.println("<form method='POST' action='"+req.getContextPath()+"/verify?action=resend' style='margin-top:12px'>");
        o.println("<button class='btn btn-green' style='width:100%'>Resend Code</button>");
        o.println("</form>");
        o.println("</div></div>");
        o.println(Html.bottom());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("unverified_id") == null) {
            resp.sendRedirect(req.getContextPath()+"/login");
            return;
        }
        int custId = (int) s.getAttribute("unverified_id");
        CustomerDAO dao = new CustomerDAO();
        Customer cu = dao.findById(custId);
        if (cu == null) { resp.sendRedirect(req.getContextPath()+"/login?error=User+not+found"); return; }

        String action = req.getParameter("action");
        if ("resend".equals(action)) {
            String code = TwilioHelper.generateCode();
            s.setAttribute("verify_code", code);
            try {
                TwilioHelper.sendSms(cu.getSid(), cu.getToken(),
                        cu.getMsisdn(), cu.getMsisdn(), "Your new code: " + code);
                resp.sendRedirect(req.getContextPath()+"/verify?info=New+code+sent!");
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath()+"/verify?error=SMS+failed:+" + e.getMessage());
            }
            return;
        }

        String code      = req.getParameter("code");
        String expected   = (String) s.getAttribute("verify_code");
        if (expected == null || !expected.equals(code)) {
            resp.sendRedirect(req.getContextPath()+"/verify?error=Invalid+code");
            return;
        }

        cu.setVerified(true);
        s.removeAttribute("unverified_id");
        s.removeAttribute("verify_code");
        s.setAttribute("customer", cu);
        LogDAO.log(req.getRemoteAddr(), "VERIFIED: " + cu.getEmail());
        resp.sendRedirect(req.getContextPath()+"/customer/home");
    }
}