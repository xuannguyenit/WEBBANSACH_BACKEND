package com.xuannguyen.identity.service.orderservice;

import com.xuannguyen.identity.controller.order.RevenueDto;
import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.order.OrderUserRequest;
import com.xuannguyen.identity.dto.response.orderresponse.DailyRevenueResponse;
import com.xuannguyen.identity.entity.Order;
import com.xuannguyen.identity.entity.OrderDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    List<Order> getOrderUserByUserId(Long userId);

    List<Order> getOrderUserByOrderIdComplete();

    List<OrderDetail> getProductOfUser();

    List<OrderDetail> getAllOderDetail(String orderId);

    Order getOrderById(String id);

    Order createOrderFromCart(OrderUserRequest orderRequest);

    Order updateOrder(String id);

    List<Order> getAllOrderByUserId();

    RevenueDto getRevenueStatistics();

    BigDecimal getRevenueStatisticsByTime(LocalDate dateStart, LocalDate dateEnd);

    List<Order> getAllOrderDesc();

    List<DailyRevenueResponse> getDailyRevenueForMonth(int month, int year);

    Long getCountOrder();

    List<OrderDetail> getListOrderDetailByOrderStatus();

    ApiResponse<Boolean> deleteOrder(String id);
}
