package com.xuannguyen.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Product extends BaseEntity{
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private String id;
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    @Column (name = "description", columnDefinition = "TEXT")
    private String description;
    private BigDecimal price;
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = true)
    private Brand brand;
    @ManyToMany
    @JoinTable(name = "product_image",joinColumns = @JoinColumn(name="product_id"),inverseJoinColumns = @JoinColumn(name="image_id"))
    private Set<Image> images = new HashSet<>();
    private LocalDate createDate = LocalDate.now();
    // sản pham thuoc user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
