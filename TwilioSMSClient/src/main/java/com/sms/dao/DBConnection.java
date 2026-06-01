/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author mohesham
 */

public class DBConnection {
    private static final String URL  = "jdbc:postgresql://ep-rough-meadow-al3gfr4v-pooler.c-3.eu-central-1.aws.neon.tech/sms?user=neondb_owner&password=npg_9coMTQXK5guk&sslmode=require&channelBinding=require";
    private static final String USER = "postgres";
    private static final String PASS = "postgres"; // Change to your password

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver not found!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}