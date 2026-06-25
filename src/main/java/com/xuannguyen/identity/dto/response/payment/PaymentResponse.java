package com.xuannguyen.identity.dto.response.payment;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String status;

    private String message;

    private String url;

    private String txnRef;

    private String orderId;
}