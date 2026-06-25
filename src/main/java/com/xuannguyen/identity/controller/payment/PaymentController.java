package com.xuannguyen.identity.controller.payment;

import com.xuannguyen.identity.configuration.VnPay;
import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.response.payment.PayResponse;
import com.xuannguyen.identity.dto.response.payment.PaymentResponse;
import com.xuannguyen.identity.service.paymentservice.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/create/{orderId}")
    public ApiResponse<PaymentResponse> createPayment(
            @PathVariable String orderId,
            HttpServletRequest request
    ) {
        String ipAddress = VnPay.getIpAddress(request);

        return ApiResponse.<PaymentResponse>builder()
                .message("Create VNPAY payment url successfully")
                .result(paymentService.createVnPayPayment(orderId, ipAddress))
                .build();
    }

    @GetMapping("/vnpay-return")
    public ApiResponse<PayResponse> vnpayReturn(
            @RequestParam Map<String, String> params
    ) {
        return ApiResponse.<PayResponse>builder()
                .message("Handle VNPAY return successfully")
                .result(paymentService.handleVnPayReturn(params))
                .build();
    }

    @GetMapping("/payment-success")
    public String paymentSuccess() {
        return "payment-success";
    }
}