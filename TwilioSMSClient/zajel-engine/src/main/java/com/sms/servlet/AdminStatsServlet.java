package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/admin/stats")
public class AdminStatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Admin admin = (Admin) req.getSession().getAttribute("admin");
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        CustomerDAO cuDao  = new CustomerDAO();
        MsgDAO      msgDao = new MsgDAO();

        List<Customer>       customers = cuDao.findAll();
        Map<Integer,Integer> counts    = msgDao.getSmsCounts();

        // total & sort descending by SMS count
        int totalSms = 0;
        for (int c : counts.values()) totalSms += c;

        customers.sort((a, b) -> {
            int ca = counts.getOrDefault(a.getCustomerId(), 0);
            int cb = counts.getOrDefault(b.getCustomerId(), 0);
            return Integer.compare(cb, ca);
        });

        String avgSms = customers.isEmpty()
                ? "0"
                : String.format("%.1f", (double) totalSms / customers.size());

        req.setAttribute("customers", customers);
        req.setAttribute("counts",    counts);
        req.setAttribute("totalSms",  totalSms);
        req.setAttribute("avgSms",    avgSms);

        req.getRequestDispatcher("/WEB-INF/views/admin/stats.jsp").forward(req, resp);
    }
}
