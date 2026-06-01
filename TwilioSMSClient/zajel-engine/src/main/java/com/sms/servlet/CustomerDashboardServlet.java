/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.sms.servlet;

import com.sms.dao.*;
import com.sms.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 *
 * @author mohesham
 */

@WebServlet("/customer/dashboard-data")
public class CustomerDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"unauthorized\"}");
            return;
        }

        Customer cu = (Customer) session.getAttribute("customer");
        // Refresh from DB
        cu = new CustomerDAO().findById(cu.getCustomerId());

        MsgDAO msgDao = new MsgDAO();

        // Total messages
        int totalMsgs = msgDao.countBySenderId(cu.getCustomerId());

        // This month count
        int thisMonth = countThisMonth(cu.getCustomerId());

        // Recent messages (last 5)
        List<Msg> allMsgs = msgDao.findBySenderId(cu.getCustomerId());
        int recentLimit = Math.min(allMsgs.size(), 5);

        // Last message time
        String lastMsgTime = "\u2014";
        if (!allMsgs.isEmpty() && allMsgs.get(0).getMsgStamp() != null) {
            lastMsgTime = allMsgs.get(0).getMsgStamp().toInstant().toString();
        }

        // ── Build JSON Response ─────────────────
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter o = resp.getWriter();

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"name\":").append(jsonStr(cu.getName())).append(",");
        json.append("\"email\":").append(jsonStr(cu.getEmail())).append(",");
        json.append("\"msisdn\":").append(jsonStr(cu.getMsisdn())).append(",");
        json.append("\"sid\":").append(jsonStr(cu.getSid())).append(",");
        json.append("\"token\":\"••••••••\",");
        json.append("\"senderid\":").append(jsonStr(cu.getMsisdn())).append(",");
        json.append("\"totalMsgs\":").append(totalMsgs).append(",");
        json.append("\"thisMonth\":").append(thisMonth).append(",");
        json.append("\"lastMsgTime\":").append(jsonStr(lastMsgTime)).append(",");

        // Recent messages array
        json.append("\"recentMsgs\":[");
        for (int i = 0; i < recentLimit; i++) {
            Msg m = allMsgs.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{");
            json.append("\"to\":").append(jsonStr(m.getReceiverMsisdn())).append(",");
            json.append("\"body\":").append(jsonStr(truncate(m.getMsgBody(), 60))).append(",");
            json.append("\"time\":").append(
                    m.getMsgStamp() != null
                    ? jsonStr(m.getMsgStamp().toInstant().toString())
                    : "null"
            ).append(",");
            json.append("\"status\":").append(jsonStr(m.getStatus() != null ? m.getStatus() : "FAILED"));
            json.append("}");
        }
        json.append("]");

        json.append("}");

        o.print(json.toString());
    }

    // ── Count messages this month ───────────────
    private int countThisMonth(int customerId) {
        String sql = "SELECT COUNT(*) FROM msgs WHERE sender_id=? "
                + "AND msg_stamp >= date_trunc('month', CURRENT_TIMESTAMP)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ── JSON string helper ──────────────────────
    private String jsonStr(String s) {
        if (s == null) {
            return "null";
        }
        return "\"" + s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }

    // ── Truncate ────────────────────────────────
    private String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() > max ? s.substring(0, max) + "\u2026" : s;
    }
}
