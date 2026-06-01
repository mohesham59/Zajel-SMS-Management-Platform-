package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/customer/history")
public class SmsHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String fromSearch = req.getParameter("from");
        String toSearch = req.getParameter("to");
        String dateFrom = req.getParameter("date_from");
        String dateTo = req.getParameter("date_to");

        boolean isSearch = (fromSearch != null && !fromSearch.isEmpty())
                || (toSearch != null && !toSearch.isEmpty())
                || (dateFrom != null && !dateFrom.isEmpty())
                || (dateTo != null && !dateTo.isEmpty());

        MsgDAO msgDao = new MsgDAO();
        List<Msg> msgs;
        if (isSearch) {
            msgs = msgDao.search(cu.getCustomerId(), toSearch, dateFrom, dateTo);
        } else {
            msgs = msgDao.findBySenderId(cu.getCustomerId());
        }

        req.setAttribute("msgs", msgs);
        req.getRequestDispatcher("/WEB-INF/views/customer/history.jsp").forward(req, resp);
    }
}