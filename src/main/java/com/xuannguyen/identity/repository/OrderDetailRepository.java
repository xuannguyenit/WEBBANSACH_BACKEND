package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,String> {
    List<OrderDetail> findAllByOrderIdAndProductId(String orderId, String productId);
    List<OrderDetail> findByOrderId(String orderId);
}
