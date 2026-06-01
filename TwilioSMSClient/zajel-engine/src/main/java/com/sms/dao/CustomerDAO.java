package com.sms.dao;

import com.sms.model.Customer;
import com.sms.model.CustomerAddress;
import com.sms.util.HashUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // ═══════════════════════════════════════════════
    // AUTH — Compare plain text (matches your DB)
    // ═══════════════════════════════════════════════
    public Customer authenticate(String email, String password) {
        String sql = "SELECT * FROM customers WHERE email=? AND passwd=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ═══════════════════════════════════════════════
    // FIND BY ID
    // ═══════════════════════════════════════════════
    public Customer findById(int id) {
        String sql = "SELECT * FROM customers WHERE customer_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ═══════════════════════════════════════════════
    // FIND BY EMAIL
    // ═══════════════════════════════════════════════
    public Customer findByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ═══════════════════════════════════════════════
    // FIND BY MSISDN
    // ═══════════════════════════════════════════════
    public Customer findByMsisdn(String msisdn) {
        String sql = "SELECT * FROM customers WHERE msisdn=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, msisdn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ═══════════════════════════════════════════════
    // REGISTER — Using ROW() for composite type
    // ═══════════════════════════════════════════════
    public boolean register(Customer cu) {
        String sql = "INSERT INTO customers "
            + "(name,email,passwd,msisdn,birthday,job,customer_address,sid,token) "
            + "VALUES (?,?,?,?,?,?,ROW(?,?,?,?,?)::customer_address_type,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cu.getName());
            ps.setString(2, cu.getEmail());
            ps.setString(3, cu.getPasswd());
            ps.setString(4, cu.getMsisdn());

            if (cu.getBirthday() != null) {
                ps.setDate(5, cu.getBirthday());
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setString(6, cu.getJob());

            // ROW() parameters — each address field separately
            CustomerAddress addr = cu.getCustomerAddress();
            if (addr != null) {
                ps.setString(7,  safe(addr.getStreet()));
                ps.setString(8,  safe(addr.getCity()));
                ps.setString(9,  safe(addr.getState()));
                ps.setString(10, safe(addr.getZip()));
                ps.setString(11, safe(addr.getCountry()));
            } else {
                ps.setString(7,  "");
                ps.setString(8,  "");
                ps.setString(9,  "");
                ps.setString(10, "");
                ps.setString(11, "");
            }

            ps.setString(12, cu.getSid());
            ps.setString(13, cu.getToken());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) cu.setCustomerId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO.register] SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ═══════════════════════════════════════════════
    // UPDATE PROFILE — Using ROW() for composite type
    // ═══════════════════════════════════════════════
    public boolean update(Customer cu) {
        String sql = "UPDATE customers SET "
            + "name=?, email=?, msisdn=?, birthday=?, job=?, "
            + "customer_address=ROW(?,?,?,?,?)::customer_address_type, "
            + "sid=?, token=? "
            + "WHERE customer_id=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, cu.getName());
            ps.setString(2, cu.getEmail());
            ps.setString(3, cu.getMsisdn());

            // Birthday — null-safe
            if (cu.getBirthday() != null) {
                ps.setDate(4, cu.getBirthday());
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(5, cu.getJob());

            // ROW() parameters — each address field as separate ?
            CustomerAddress addr = cu.getCustomerAddress();
            if (addr != null) {
                ps.setString(6,  safe(addr.getStreet()));
                ps.setString(7,  safe(addr.getCity()));
                ps.setString(8,  safe(addr.getState()));
                ps.setString(9,  safe(addr.getZip()));
                ps.setString(10, safe(addr.getCountry()));
            } else {
                ps.setString(6,  "");
                ps.setString(7,  "");
                ps.setString(8,  "");
                ps.setString(9,  "");
                ps.setString(10, "");
            }

            ps.setString(11, cu.getSid());
            ps.setString(12, cu.getToken());
            ps.setInt(13, cu.getCustomerId());

            System.out.println("[CustomerDAO.update] Updating customer_id=" + cu.getCustomerId());
            int rows = ps.executeUpdate();
            System.out.println("[CustomerDAO.update] Rows affected: " + rows);
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[CustomerDAO.update] SQL ERROR for customer_id=" + cu.getCustomerId());
            System.err.println("[CustomerDAO.update] SQLState: " + e.getSQLState());
            System.err.println("[CustomerDAO.update] Message: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ═══════════════════════════════════════════════
    // UPDATE PASSWORD — Plain text
    // ═══════════════════════════════════════════════
    public boolean updatePassword(int customerId, String newPassword) {
        String sql = "UPDATE customers SET passwd=? WHERE customer_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ═══════════════════════════════════════════════
    // DELETE — Cascade-safe (msgs has ON DELETE CASCADE,
    //          but being explicit is safer)
    // ═══════════════════════════════════════════════
    public boolean delete(int id) {
        String delMsgs = "DELETE FROM msgs WHERE sender_id=?";
        String delCust = "DELETE FROM customers WHERE customer_id=?";
        try (Connection c = DBConnection.getConnection()) {
            // Delete related messages first
            try (PreparedStatement ps = c.prepareStatement(delMsgs)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            // Delete customer
            try (PreparedStatement ps = c.prepareStatement(delCust)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ═══════════════════════════════════════════════
    // FIND ALL
    // ═══════════════════════════════════════════════
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY created_at DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ═══════════════════════════════════════════════
    // MAPPER — Reads composite type from result set
    // ═══════════════════════════════════════════════
    private Customer map(ResultSet rs) throws SQLException {
        Customer cu = new Customer();
        cu.setCustomerId(rs.getInt("customer_id"));
        cu.setName(rs.getString("name"));
        cu.setEmail(rs.getString("email"));
        cu.setPasswd(rs.getString("passwd"));
        cu.setMsisdn(rs.getString("msisdn"));
        cu.setBirthday(rs.getDate("birthday"));
        cu.setJob(rs.getString("job"));

        // Parse composite type customer_address
        String addrStr = rs.getString("customer_address");
        cu.setCustomerAddress(CustomerAddress.fromPostgresLiteral(addrStr));

        cu.setSid(rs.getString("sid"));
        cu.setToken(rs.getString("token"));
        cu.setCreatedAt(rs.getTimestamp("created_at"));

        cu.setVerified(true);

        return cu;
    }

    // ═══════════════════════════════════════════════
    // NULL-SAFE HELPER
    // ═══════════════════════════════════════════════
    private static String safe(String s) {
        return s != null ? s : "";
    }
}