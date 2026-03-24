package com.flashsale.java.run;

import com.flashsale.java.business.service.OrderServiceImpl;
import com.flashsale.java.business.service.ProductServiceImpl;
import com.flashsale.java.business.service.UserServiceImpl;
import com.flashsale.java.utils.DatabaseConnectionManager;
import com.flashsale.java.utils.InputMethod;

import java.util.Scanner;

public class MenuPrinter {
    public static OrderServiceImpl orderService = new OrderServiceImpl();
    public static ProductServiceImpl productService = new ProductServiceImpl();
    public static UserServiceImpl userService = new UserServiceImpl();

    public static void printMenu(Scanner scanner) {
        int choice;
        do {
            System.out.println("==================FLASH SALE MANAGEMENT==================");
            System.out.println("1. Khởi tạo hệ thống");
            System.out.println("2. Quản lý sản phẩm");
            System.out.println("3. Quản lý khách hàng");
            System.out.println("4. Đặt hàng Flash Sale");
            System.out.println("5. Chạy Stress Test");
            System.out.println("6. Báo cáo & thống kê");
            System.out.println("0. Thoát");
            System.out.println("========================================================");
            choice = InputMethod.getInputInt("Lựa chọn của bạn : ");
            switch (choice) {
                case 1:
                    DatabaseConnectionManager.initDB("src/script.sql");
                    break;
                case 2:
                    ProductMenu.printMenu(scanner,productService);
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
                    break;
            }
        } while (choice != 0);
    }

}
