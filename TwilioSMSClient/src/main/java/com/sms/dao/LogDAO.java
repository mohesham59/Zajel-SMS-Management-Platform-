/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.dao;

import com.sms.model.Log;
import java.sql.*;
import java.util.List;

/**
 *
 * @author mohesham
 */
public class LogDAO {

    public boolean save(Log log) {
        String sql = "INSERT INTO logs (rqst_ip, log_body) VALUES (?::inet, ?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, log.getRqstIp());
            ps.setString(2, log.getLogBody());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void log(String ip, String body) {
        Log l = new Log();
        l.setRqstIp(ip);
        l.setLogBody(body);
        new LogDAO().save(l);
    }

    /**
     * Get recent log entries
     * @param limit
     * @return 
     */
    public static List<com.sms.model.Log> getRecentLogs(int limit) {
        List<com.sms.model.Log> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM logs ORDER BY log_stamp DESC LIMIT ?";
        try (java.sql.Connection c = DBConnection.getConnection(); java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                com.sms.model.Log log = new com.sms.model.Log();
                log.setLogId(rs.getInt("log_id"));
                log.setRqstIp(rs.getString("rqst_ip"));
                log.setLogBody(rs.getString("log_body"));
                log.setLogStamp(rs.getTimestamp("log_stamp"));
                list.add(log);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
