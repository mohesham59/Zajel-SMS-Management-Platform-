package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.Customer;
import com.sms.util.*;
import com.twilio.Twilio; // Import Twilio SDK

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.Properties;

@WebServlet("/verify")
public class VerifyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("unverified_id") == null) {
            resp.sendRedirect(req.getContextPath()+"/login");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/auth/verify.jsp").forward(req, resp);
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
        if (cu == null) { 
            resp.sendRedirect(req.getContextPath()+"/login?error=User+not+found"); 
            return; 
        }

        String action = req.getParameter("action");
        if ("resend".equals(action)) {
            String code = TwilioHelper.generateCode();
            s.setAttribute("verify_code", code);
            
            try (InputStream input = TwilioHelper.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new IllegalStateException("Unable to find config.properties in classpath.");
                }
                Properties props = new Properties();
                props.load(input);

                String accountSid = props.getProperty("twilio.sid");
                String authToken = props.getProperty("twilio.token");
                String twilioNumber = props.getProperty("twilio.number");

                if (accountSid == null || authToken == null || twilioNumber == null) {
                    throw new IllegalStateException("One or more Twilio keys are missing from config.properties!");
                }

                Twilio.init(accountSid, authToken);
                
                TwilioHelper.sendSms(accountSid, authToken, twilioNumber, cu.getMsisdn(), "Your new code: " + code);
                
                resp.sendRedirect(req.getContextPath()+"/verify?info=New+code+sent!");
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendRedirect(req.getContextPath()+"/verify?error=SMS+failed:+" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
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