package com.xuannguyen.identity.entity;

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
    @JoinColumn(name = "cart_id")
    private Cart cart;
    private String productId; // map vơi id của bảng product của bên product service
    private int quantity;
    private BigDecimal price;

}
