package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/admin/home")
public class AdminHomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Admin admin = (Admin) req.getSession().getAttribute("admin");
        if (admin == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        CustomerDAO cuDao = new CustomerDAO();
        MsgDAO msgDao = new MsgDAO();

        List<Customer> customers = cuDao.findAll();
        Map<Integer, Integer> smsCounts = msgDao.getSmsCounts();

        int totalCustomers = customers.size();
        int totalSms = msgDao.countAll();
        int smsToday = msgDao.countToday();
        int successCount = msgDao.countByStatus("SUCCESS");
        String deliveryRate = totalSms > 0
                ? String.format("%.1f%%", (successCount * 100.0 / totalSms))
                : "N/A";

        int activeCustomers = 0;
        for (Customer cu : customers) {
            if (smsCounts.getOrDefault(cu.getCustomerId(), 0) > 0) {
                activeCustomers++;
            }
        }

        customers.sort((a, b) -> {
            int ca = smsCounts.getOrDefault(a.getCustomerId(), 0);
            int cb = smsCounts.getOrDefault(b.getCustomerId(), 0);
            return Integer.compare(cb, ca);
        });

        int maxSms = 0;
        for (int c : smsCounts.values()) { if (c > maxSms) maxSms = c; }

        List<Log> recentLogs = LogDAO.getRecentLogs(8);

        req.setAttribute("customers", customers);
        req.setAttribute("smsCounts", smsCounts);
        req.setAttribute("totalCustomers", totalCustomers);
        req.setAttribute("totalSms", totalSms);
        req.setAttribute("smsToday", smsToday);
        req.setAttribute("successCount", successCount);
        req.setAttribute("deliveryRate", deliveryRate);
        req.setAttribute("activeCustomers", activeCustomers);
        req.setAttribute("maxSms", maxSms);
        req.setAttribute("recentLogs", recentLogs);

        req.getRequestDispatcher("/WEB-INF/views/admin/home.jsp").forward(req, resp);
    }
}