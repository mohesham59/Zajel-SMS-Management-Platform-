package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Date;

@WebServlet("/admin/customer-edit")
public class AdminCustomerEditServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Admin admin = (Admin) req.getSession().getAttribute("admin");
        if (admin == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String ctx = req.getContextPath();
        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (Exception e) {
            resp.sendRedirect(ctx + "/admin/customers?success=Invalid+customer+ID");
            return;
        }

        Customer cu = new CustomerDAO().findById(id);
        if (cu == null) {
            resp.sendRedirect(ctx + "/admin/customers?success=Customer+not+found");
            return;
        }

        CustomerAddress addr = cu.getCustomerAddress();
        if (addr == null) addr = new CustomerAddress();

        req.setAttribute("cu", cu);
        req.setAttribute("addr", addr);

        req.getRequestDispatcher("/WEB-INF/views/admin/customer-edit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String ctx = req.getContextPath();

        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (Exception e) {
            resp.sendRedirect(ctx + "/admin/customers?success=Invalid+customer+ID");
            return;
        }

        CustomerDAO dao = new CustomerDAO();
        Customer cu = dao.findById(id);
        if (cu == null) {
            resp.sendRedirect(ctx + "/admin/customers?success=Customer+not+found");
            return;
        }

        try {
            cu.setName(req.getParameter("name"));
            cu.setEmail(req.getParameter("email"));
            cu.setMsisdn(req.getParameter("msisdn"));

            String bday = req.getParameter("birthday");
            if (bday != null && !bday.trim().isEmpty()) {
                try {
                    cu.setBirthday(Date.valueOf(bday.trim()));
                } catch (IllegalArgumentException e) {
                    System.err.println("[AdminEdit] Bad date format: " + bday);
                    cu.setBirthday(null);
                }
            } else {
                cu.setBirthday(null);
            }

            cu.setJob(req.getParameter("job"));

            CustomerAddress addr = new CustomerAddress(
                safe(req.getParameter("street")),
                safe(req.getParameter("city")),
                safe(req.getParameter("state")),
                safe(req.getParameter("zip")),
                safe(req.getParameter("country"))
            );
            cu.setCustomerAddress(addr);

            System.out.println("[AdminEdit] Address literal: " + addr.toPostgresLiteral());

            cu.setSid(req.getParameter("sid"));
            cu.setToken(req.getParameter("token"));

            if (dao.update(cu)) {
                LogDAO.log(req.getRemoteAddr(), "ADMIN_EDIT customer=" + id);
                resp.sendRedirect(ctx + "/admin/customer-edit?id=" + id
                        + "&success=Customer+updated+successfully");
            } else {
                resp.sendRedirect(ctx + "/admin/customer-edit?id=" + id
                        + "&error=Update+failed.+Check+server+logs+for+SQL+details.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            String errMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            resp.sendRedirect(ctx + "/admin/customer-edit?id=" + id
                    + "&error=" + java.net.URLEncoder.encode("Error: " + errMsg, "UTF-8"));
        }
    }

    private static String safe(String s) {
        return s != null ? s : "";
    }
}