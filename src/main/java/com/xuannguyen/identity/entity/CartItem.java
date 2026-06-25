package com.xuannguyen.identity.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false) // ánh xạ với cột `cart_id` trong database
    @JsonBackReference
    private Cart cart;
    private String productId;
    private String productName;
    private BigDecimal productPrice;
    private int productQuantity;
    private long discountPercentage;
    private BigDecimal totalPrice;
    private Long userId;

}
