package com.flashsale.java.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseConnectionManager {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/flash_sale?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123";
    private static Connection connection;

    //Singleton
    public static Connection getConnection() {
        try {
            if (connection == null) {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Đã tạo kết nối");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi : Chưa cài đặt MySQL Driver");
        } catch (SQLException e) {
            System.err.println("Lỗi SQL : Kết nối thất bại");
        }
        return connection;
    }

    // hàm tự động đọc file
    public static void initDB(String filePath) {
        try {
            // Đọc toàn bộ nội dung file SQL
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));

            // Xóa các dòng comment để tránh lỗi vặt
            content = content.replaceAll("(?m)^--.*", "");

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                // Mẹo: Tách các câu lệnh theo dấu ;
                // Nhưng đối với Procedure/Function, ta cần gửi nguyên khối.
                // Một cách đơn giản là tách theo dấu ; và lọc bỏ các khoảng trắng
                String[] queries = content.split(";");

                StringBuilder currentQuery = new StringBuilder();
                for (String query : queries) {
                    currentQuery.append(query);
                    String sql = currentQuery.toString().trim();

                    if (sql.isEmpty()) continue;

                    // Kiểm tra xem câu lệnh đã kết thúc thực sự chưa (tránh cắt ngang BEGIN...END)
                    // Nếu chứa BEGIN mà chưa có END thì cộng dồn tiếp
                    if (sql.toUpperCase().contains("BEGIN") && !sql.toUpperCase().contains("END")) {
                        currentQuery.append(";");
                        continue;
                    }

                    stmt.execute(sql);
                    currentQuery.setLength(0); // Reset
                }
                System.out.println("Thực thi script " + filePath + " thành công!");
            }
        } catch (Exception e) {
            System.err.println("Lỗi thực thi SQL: " + e.getMessage());
        }
    }
}




