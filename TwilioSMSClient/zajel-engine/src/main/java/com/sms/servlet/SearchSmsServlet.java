package com.sms.servlet;

import com.sms.dao.MsgDAO;
import com.sms.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/customer/search")
public class SearchSmsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String receiver = req.getParameter("receiver");
        String dateFrom = req.getParameter("date_from");
        String dateTo   = req.getParameter("date_to");
        boolean searched = (receiver != null || dateFrom != null || dateTo != null);

        if (searched) {
            List<Msg> results = new MsgDAO().search(cu.getCustomerId(), receiver, dateFrom, dateTo);
            req.setAttribute("results", results);
        }

        req.getRequestDispatcher("/WEB-INF/views/customer/search.jsp").forward(req, resp);
    }
}