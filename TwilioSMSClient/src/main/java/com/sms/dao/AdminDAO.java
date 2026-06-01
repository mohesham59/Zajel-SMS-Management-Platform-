/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.dao;
import com.sms.model.Admin;
import com.sms.util.HashUtil;
import java.sql.*;
/**
 *
 * @author mohesham
 */

public class AdminDAO {

    public Admin authenticate(String email, String password) {
        String sql = "SELECT * FROM admins WHERE email=? AND passwd_hash=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, HashUtil.sha256(password));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Admin findById(int id) {
        String sql = "SELECT * FROM admins WHERE admin_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Admin map(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setAdminId(rs.getInt("admin_id"));
        a.setName(rs.getString("name"));
        a.setEmail(rs.getString("email"));
        a.setPasswdHash(rs.getString("passwd_hash"));
        a.setSuper(rs.getBoolean("is_super"));
        return a;
    }
}