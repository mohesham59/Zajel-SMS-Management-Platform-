package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.Admin;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/customer-delete")
public class AdminCustomerDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Verify admin is logged in
        Admin admin = (Admin) req.getSession().getAttribute("admin");
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int id = Integer.parseInt(req.getParameter("id"));
        new CustomerDAO().delete(id);
        LogDAO.log(req.getRemoteAddr(), "ADMIN_DELETE customer=" + id);
        resp.sendRedirect(req.getContextPath() + "/admin/customers?success=Customer+deleted");
    }
}