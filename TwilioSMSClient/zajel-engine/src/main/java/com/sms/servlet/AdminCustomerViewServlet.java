package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/admin/customer-view")
public class AdminCustomerViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Admin admin = (Admin) req.getSession().getAttribute("admin");
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String ctx = req.getContextPath();
        int id = Integer.parseInt(req.getParameter("id"));
        Customer cu = new CustomerDAO().findById(id);
        int smsCount = new MsgDAO().countBySenderId(id);

        if (cu == null) {
            resp.sendRedirect(ctx + "/admin/customers?success=Customer+not+found");
            return;
        }

        CustomerAddress addr = cu.getCustomerAddress();
        if (addr == null) {
            addr = new CustomerAddress();
        }

        req.setAttribute("cu", cu);
        req.setAttribute("addr", addr);
        req.setAttribute("smsCount", smsCount);

        req.getRequestDispatcher("/WEB-INF/views/admin/customer-view.jsp").forward(req, resp);
    }
}
