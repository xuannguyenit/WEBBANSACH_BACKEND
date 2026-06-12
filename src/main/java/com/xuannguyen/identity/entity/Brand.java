package com.xuannguyen.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table (name = "brand")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private boolean enable;
    @OneToMany(mappedBy = "brand")
    private List<Product> products = new ArrayList<>();

}
