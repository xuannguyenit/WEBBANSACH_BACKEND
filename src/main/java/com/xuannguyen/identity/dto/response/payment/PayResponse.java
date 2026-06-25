package com.xuannguyen.identity.dto.response.payment;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayResponse {
    private String orderId;

    private String txnRef;

    private String transactionNo;

    private String responseCode;

    private String status;

    private String message;
}
