/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.filter;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

/**
 *
 * @author mohesham
 */



@WebFilter(urlPatterns = {"/customer/*", "/admin/*"})
public class AuthFilter implements Filter {

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        String path = request.getRequestURI();

        /* ── Admin paths ──────────────────────── */
        if (path.contains("/admin/")) {
            if (session == null || session.getAttribute("admin") == null) {
                response.sendRedirect(request.getContextPath() + "/login?error=Please+login+as+Admin");
                return;
            }
        }

        /* ── Customer paths ───────────────────── */
        if (path.contains("/customer/")) {
            if (session == null || session.getAttribute("customer") == null) {
                response.sendRedirect(request.getContextPath() + "/login?error=Please+login+first");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}