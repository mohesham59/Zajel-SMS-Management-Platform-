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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
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
