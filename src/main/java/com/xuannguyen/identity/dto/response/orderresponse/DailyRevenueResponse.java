package com.xuannguyen.identity.dto.response.orderresponse;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyRevenueResponse {
    private LocalDate date;

    private BigDecimal revenue;
}
