package com.xuannguyen.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;


    private String redirectUrl;

    private Integer sortOrder;

    private Boolean active;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;
}
