package com.xuannguyen.identity.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;

    private String firstname; // bắt buộc

    private String lastname;

    private String country; // bắt buộc

    private String address;// địa chỉ người nhận bắt buộc

    private String town;

    private String state;// tên khu vực

    private long postCode;// mã bưu chính

    private String email; // bắt buộc

    private String phone; // băt buộc

    private String note;// chú ý

    private long totalPrice;

    private String paymentmethod; // phương thức thanh toán

    private String active;// trạng thái đơn hàng

    @OneToMany(mappedBy="order")
    @JsonBackReference
    private Set<Cart> cart;
}
