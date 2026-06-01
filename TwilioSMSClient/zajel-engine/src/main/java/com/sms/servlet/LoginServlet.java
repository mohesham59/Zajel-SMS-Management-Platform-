package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;
import com.sms.util.Html;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Already logged in? Redirect
        HttpSession s = req.getSession(false);
        if (s != null) {
            if (s.getAttribute("admin") != null) {
                resp.sendRedirect(req.getContextPath() + "/admin/home");
                return;
            }
            if (s.getAttribute("customer") != null) {
                resp.sendRedirect(req.getContextPath() + "/customer/home");
                return;
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    // ═══════════════════════════════════════════════
    // POST — Handle login
    // ═══════════════════════════════════════════════
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String pass = req.getParameter("password");
        String role = req.getParameter("role");
        String ctx = req.getContextPath();

        LogDAO.log(req.getRemoteAddr(), "LOGIN attempt: " + email + " role=" + role);

        if ("admin".equals(role)) {
            Admin admin = new AdminDAO().authenticate(email, pass);
            if (admin == null) {
                resp.sendRedirect(ctx + "/login?error=Invalid+admin+credentials");
                return;
            }
            HttpSession s = req.getSession(true);
            s.setAttribute("admin", admin);
            s.setMaxInactiveInterval(30 * 60);
            resp.sendRedirect(ctx + "/admin/home");

        } else {
            Customer cu = new CustomerDAO().authenticate(email, pass);
            if (cu == null) {
                resp.sendRedirect(ctx + "/login?error=Invalid+email+or+password");
                return;
            }
            if (!cu.isVerified()) {
                HttpSession s = req.getSession(true);
                s.setAttribute("unverified_id", cu.getCustomerId());
                resp.sendRedirect(ctx + "/verify");
                return;
            }
            HttpSession s = req.getSession(true);
            s.setAttribute("customer", cu);
            s.setMaxInactiveInterval(30 * 60);
            resp.sendRedirect(ctx + "/customer/home");
        }
    }
}
