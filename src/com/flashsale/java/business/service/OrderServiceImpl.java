package com.flashsale.java.business.service;

import com.flashsale.java.business.dao.*;
import com.flashsale.java.entity.OrderDetails;
import com.flashsale.java.entity.Orders;
import com.flashsale.java.entity.Products;
import com.flashsale.java.utils.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class OrderServiceImpl implements IOrderService {
    IOrderDao orderDao = new OrderDAO();
    ProductDAO productDao = new ProductDAO();
    @Override
    public List<Orders> findAllOrders() {
        return orderDao.getAllOrder(DatabaseConnectionManager.getConnection());
    }

    @Override
    public List<Products> findAllProductDetails() {
        return productDao.getAll();
    }

    @Override
    public void placeOrder(int userId, List<CardItem> items) {
        Connection conn = null;
        try
        {
            if (items.isEmpty()){
                System.out.println("Danh sách trống kìa stupid v !");
                return;
            }
            conn = DatabaseConnectionManager.getConnection();
            if (conn == null){
                throw new RuntimeException("Có mở được kết nối đâu trời ????");
            }
            conn.setAutoCommit(false);

            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            for(CardItem p : items){
                PreparedStatement ps =
                        conn.prepareStatement("update Products set stock = stock - ? where id = ? and stock >= ?");
                ps.setInt(1, p.getQuantity());
                ps.setInt(2, p.getProductId());
                ps.setInt(3, p.getQuantity());

                int check = ps.executeUpdate();
                if(check == 0){
                    conn.rollback();
                    throw new RuntimeException("Đã hết hàng ! , ăn nem chua thêm tu vi đi");
                }
            }

            PreparedStatement psOrder =
                    conn.prepareStatement("insert into Orders (user_id, total_amount, order_date, status) values (?,?,?,?)" ,
                            PreparedStatement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, userId);
            double totalAmount = 0.0;
            for(CardItem item : items){
                Products prDB = productDao.findById(item.getProductId());
                if (prDB == null) {
                    conn.rollback();
                    throw new RuntimeException("Không tìm thấy sản phẩm , ăn nem chua thêm tu vi đi");
                }
                totalAmount += prDB.getPrice() * item.getQuantity();
            }
            psOrder.setDouble(2, totalAmount);
            psOrder.setObject(3, LocalDateTime.now());
            psOrder.setString(4, "PENDING");

            int check = psOrder.executeUpdate();

            if (check == 0){
                conn.rollback();
                throw new RuntimeException("Không thêm được order , ăn nem chua thêm tu vi đi");
            }

            int orderId;
            ResultSet rs = psOrder.getGeneratedKeys();
            if  (rs.next()) {
                orderId = rs.getInt(1);
            } else {
                conn.rollback();
                throw new RuntimeException("Không lấy được orderId , ăn nem chua thêm tu vi đi");
            }

            PreparedStatement psOrderDetail =
                    conn.prepareStatement("insert into Order_Details(order_id, product_id, quantity, unit_price) values (?,?,?,?)");

            for(CardItem item : items){
                Products prDB = productDao.findById(item.getProductId());
                if (prDB == null) {
                    conn.rollback();
                    throw new RuntimeException("Không tìm thấy sản phẩm rồi bảo bối à");
                }

                psOrderDetail.setInt(1, orderId);
                psOrderDetail.setInt(2, item.getProductId());
                psOrderDetail.setInt(3, item.getQuantity());
                psOrderDetail.setDouble(4,prDB.getPrice());

                psOrderDetail.addBatch();
            }
            psOrderDetail.executeBatch();

            conn.commit();
            System.out.println("Thành công rồi hú hú ");
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if  (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
