/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.dao;
import com.sms.model.ErrCod;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mohesham
 */


public class ErrCodDAO {

    public ErrCod findById(int id) {
        String sql = "SELECT * FROM errcod WHERE error_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public ErrCod findByMsg(String msg) {
        String sql = "SELECT * FROM errcod WHERE error_msg=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, msg);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<ErrCod> findAll() {
        List<ErrCod> list = new ArrayList<>();
        String sql = "SELECT * FROM errcod ORDER BY error_id";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private ErrCod map(ResultSet rs) throws SQLException {
        ErrCod e = new ErrCod();
        e.setErrorId(rs.getInt("error_id"));
        e.setErrorMsg(rs.getString("error_msg"));
        e.setDescription(rs.getString("description"));
        return e;
    }
}