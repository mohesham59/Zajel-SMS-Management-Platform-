/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.dao;

import com.sms.model.Msg;
import java.sql.*;
import java.util.*;

/**
 *
 * @author mohesham
 */
public class MsgDAO {

    public boolean save(Msg m) {
        String sql = "INSERT INTO msgs (sender_id,receiver_msisdn,msg_body,error_code,status) VALUES (?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getSenderId());
            ps.setString(2, m.getReceiverMsisdn());
            ps.setString(3, m.getMsgBody());
            if (m.getErrorCode() != null) {
                ps.setInt(4, m.getErrorCode());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, m.getStatus() != null ? m.getStatus() : "FAILED");
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    m.setMsgId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Msg> findBySenderId(int senderId) {
        List<Msg> list = new ArrayList<>();
        String sql = "SELECT m.*, e.error_msg FROM msgs m "
                + "LEFT JOIN errcod e ON m.error_code = e.error_id "
                + "WHERE m.sender_id=? ORDER BY m.msg_stamp DESC";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean delete(int msgId, int senderId) {
        String sql = "DELETE FROM msgs WHERE msg_id=? AND sender_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, msgId);
            ps.setInt(2, senderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Search SMS by receiver, sender_msisdn, or date range
     * @param senderId
     * @param toNum
     * @param dateFrom
     * @param dateTo
     * @return 
     */
    public List<Msg> search(int senderId, String toNum, String dateFrom, String dateTo) {
        List<Msg> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT m.*, e.error_msg FROM msgs m "
                + "LEFT JOIN errcod e ON m.error_code=e.error_id "
                + "WHERE m.sender_id=?");
        List<Object> params = new ArrayList<>();
        params.add(senderId);

        if (toNum != null && !toNum.trim().isEmpty()) {
            sql.append(" AND m.receiver_msisdn LIKE ?");
            params.add("%" + toNum.trim() + "%");
        }
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append(" AND m.msg_stamp >= ?::timestamptz");
            params.add(dateFrom.trim() + " 00:00:00");
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append(" AND m.msg_stamp <= ?::timestamptz");
            params.add(dateTo.trim() + " 23:59:59");
        }
        sql.append(" ORDER BY m.msg_stamp DESC");

        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else {
                    ps.setString(i + 1, (String) p);
                }
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countBySenderId(int senderId) {
        String sql = "SELECT COUNT(*) FROM msgs WHERE sender_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Stats: customer_id -> count
     * @return 
     */
    public Map<Integer, Integer> getSmsCounts() {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT sender_id, COUNT(*) AS cnt FROM msgs GROUP BY sender_id ORDER BY cnt DESC";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getInt("sender_id"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    private Msg map(ResultSet rs) throws SQLException {
        Msg m = new Msg();
        m.setMsgId(rs.getInt("msg_id"));
        m.setSenderId(rs.getInt("sender_id"));
        m.setReceiverMsisdn(rs.getString("receiver_msisdn"));
        m.setMsgBody(rs.getString("msg_body"));
        m.setMsgStamp(rs.getTimestamp("msg_stamp"));
        int ec = rs.getInt("error_code");
        m.setErrorCode(rs.wasNull() ? null : ec);
        try {
            m.setErrorMsg(rs.getString("error_msg"));
        } catch (SQLException ignored) {
        }
        try {
            m.setStatus(rs.getString("status"));
        } catch (SQLException ignored) {
            m.setStatus("FAILED");
        }
        return m;
    }

    /**
     * Count all SMS sent today (platform-wide)
     * @return 
     */
    public int countToday() {
        String sql = "SELECT COUNT(*) FROM msgs WHERE msg_stamp >= CURRENT_DATE";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Count all SMS (platform-wide)
     * @return 
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM msgs";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Count successful SMS (platform-wide)
     * @param status
     * @return 
     */
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM msgs WHERE status=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
