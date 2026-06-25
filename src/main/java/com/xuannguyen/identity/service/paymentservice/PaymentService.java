package com.xuannguyen.identity.service.paymentservice;

import com.xuannguyen.identity.dto.response.payment.PayResponse;
import com.xuannguyen.identity.dto.response.payment.PaymentResponse;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface PaymentService {
    PaymentResponse createVnPayPayment(String orderId, String ipAddress);

    PayResponse handleVnPayReturn(Map<String, String> params);
}
