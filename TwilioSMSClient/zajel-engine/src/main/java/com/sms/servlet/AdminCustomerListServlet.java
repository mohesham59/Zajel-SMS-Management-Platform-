package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/admin/customers")
public class AdminCustomerListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Admin admin = (Admin) req.getSession().getAttribute("admin");
        if (admin == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        List<Customer> customers = new CustomerDAO().findAll();
        Map<Integer, Integer> smsCounts = new MsgDAO().getSmsCounts();

        req.setAttribute("customers", customers);
        req.setAttribute("smsCounts", smsCounts);

        req.getRequestDispatcher("/WEB-INF/views/admin/customers.jsp").forward(req, resp);
    }
}