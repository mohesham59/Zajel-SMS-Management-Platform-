/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.sms.servlet;
import com.sms.dao.*;
import com.sms.model.Customer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 *
 * @author mohesham
 */


@WebServlet("/customer/delete-sms")
public class DeleteSmsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String idStr = req.getParameter("id");
        if (idStr != null) {
            try {
                int msgId = Integer.parseInt(idStr);
                new MsgDAO().delete(msgId, cu.getCustomerId());
                LogDAO.log(req.getRemoteAddr(),
                    "SMS_DELETE msgId=" + msgId + " by=" + cu.getCustomerId());
            } catch (NumberFormatException ignored) {}
        }

        resp.sendRedirect(req.getContextPath() + "/customer/history?success=Message+deleted");
    }
}