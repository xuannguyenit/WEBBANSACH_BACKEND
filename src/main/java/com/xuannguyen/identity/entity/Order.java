package com.xuannguyen.identity.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table (name = "orders")
public class Order {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private String id;
    private Long userId;
    private String fullName;
    @Column(name = "email")
    private String email;
    @Column (nullable = false)
    private String phone;
    @Column (name = "address",nullable = false)
    private String address;
    private LocalDateTime orderTime;
    private boolean active;
    private String status; // trạng thái đơn hàng
    private BigDecimal totalPrice;
    private LocalDateTime shippingDate; // ngày nhận hàng
    @Column(name = "transaction_reference", unique = true, nullable = true)
    private String transactionReference; // mã tham chiếu khi giao dịch thành công
    private String shippingMethod;
    private String shippingAddress;
    private String trackingNumber;
    private String paymentMethod; // phương thức thanh toán
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderDetail> orderDetails = new ArrayList<>();

}
