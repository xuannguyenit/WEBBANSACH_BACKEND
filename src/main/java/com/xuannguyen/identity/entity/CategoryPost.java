package com.xuannguyen.identity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CategoryPost {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToOne
    private Image image;
    @ManyToOne
    @JoinColumn (name = "user_id")
    private User user;
}
