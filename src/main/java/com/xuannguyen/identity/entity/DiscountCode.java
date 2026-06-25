package com.xuannguyen.identity.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class DiscountCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NonNull
    private String code;
    private long discountPercentage;
    private LocalDate createDate = LocalDate.now();
}
