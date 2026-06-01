package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Date;

@WebServlet("/customer/profile")
public class CustomerProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        cu = new CustomerDAO().findById(cu.getCustomerId());
        req.getSession().setAttribute("customer", cu);

        req.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(req, resp);
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
}