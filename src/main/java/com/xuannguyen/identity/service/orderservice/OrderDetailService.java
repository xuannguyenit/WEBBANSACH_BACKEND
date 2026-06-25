package com.xuannguyen.identity.service.orderservice;

import com.xuannguyen.identity.dto.request.order.OrderDetailRequest;
import com.xuannguyen.identity.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    // create orderdetail
    OrderDetail createOrderDetail(OrderDetailRequest request);
    // update orderdetail
    OrderDetail updateOrderDetail(String id, OrderDetailRequest request);
    // delete orderdetail
    void deleteOrderDetail(String id);
    // get all
    List<OrderDetail> getAllOrderDetail();
    // get orderdetail by oderId
    List<OrderDetail> getOrderDetailByOrderId(String id);
    OrderDetail getOrderDetailById(String id);
    OrderDetail addToCart(Long userId, OrderDetailRequest request);
}
