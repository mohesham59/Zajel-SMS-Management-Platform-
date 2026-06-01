package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;
import com.sms.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.URLEncoder;

@WebServlet("/customer/send")
public class SendSmsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        req.getRequestDispatcher("/WEB-INF/views/customer/send.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String body = req.getParameter("body");
        String ctx = req.getContextPath();

        if (from == null || from.trim().isEmpty()) from = cu.getMsisdn();

        ErrCodDAO errDao = new ErrCodDAO();
        MsgDAO msgDao = new MsgDAO();

        Msg msg = new Msg();
        msg.setSenderId(cu.getCustomerId());
        msg.setReceiverMsisdn(to);
        msg.setMsgBody(body);

        try {
            TwilioHelper.sendSms(cu.getSid(), cu.getToken(), from, to, body);
            ErrCod ok = errDao.findByMsg("SUCCESS");
            msg.setErrorCode(ok != null ? ok.getErrorId() : null);
            msg.setStatus("SUCCESS");
            msgDao.save(msg);
            LogDAO.log(req.getRemoteAddr(), "SMS_SENT by=" + cu.getCustomerId() + " from=" + from + " to=" + to);
            resp.sendRedirect(ctx + "/customer/send?success=SMS+sent+successfully!");

        } catch (com.twilio.exception.AuthenticationException e) {
            ErrCod err = errDao.findByMsg("AUTH_FAILED");
            msg.setErrorCode(err != null ? err.getErrorId() : null);
            msg.setStatus("FAILED");
            msgDao.save(msg);
            LogDAO.log(req.getRemoteAddr(), "SMS_FAIL auth by=" + cu.getCustomerId());
            resp.sendRedirect(ctx + "/customer/send?error=Authentication+failed.+Check+Twilio+credentials.");

        } catch (com.twilio.exception.ApiException e) {
            ErrCod err = errDao.findByMsg("INVALID_NUMBER");
            msg.setErrorCode(err != null ? err.getErrorId() : null);
            msgDao.save(msg);
            LogDAO.log(req.getRemoteAddr(), "SMS_FAIL api by=" + cu.getCustomerId());
            resp.sendRedirect(ctx + "/customer/send?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));

        } catch (Exception e) {
            ErrCod err = errDao.findByMsg("UNKNOWN_ERROR");
            msg.setErrorCode(err != null ? err.getErrorId() : null);
            msgDao.save(msg);
            LogDAO.log(req.getRemoteAddr(), "SMS_FAIL by=" + cu.getCustomerId() + " : " + e.getMessage());
            resp.sendRedirect(ctx + "/customer/send?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}