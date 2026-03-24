package com.flashsale.java.run;

import com.flashsale.java.utils.DatabaseConnectionManager;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    static void main(String[] args) {
        Connection conn = DatabaseConnectionManager.getConnection();
        Scanner sc = new Scanner(System.in);
        MenuPrinter.printMenu(sc);
        sc.close();
    }
}
