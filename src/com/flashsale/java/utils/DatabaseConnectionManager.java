package com.flashsale.java.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/flash_sale?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123";

    public static Connection openConnection(){
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL,USERNAME,PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi : Chưa cài đặt MySQL Driver");
        } catch (SQLException e) {
            System.err.println("Lỗi SQL : Kết nối thất bại");
        }
        return null;
    }

    public static void initDB(){}

    static void main(String[] args) {
        Connection con = openConnection();
    }
}
