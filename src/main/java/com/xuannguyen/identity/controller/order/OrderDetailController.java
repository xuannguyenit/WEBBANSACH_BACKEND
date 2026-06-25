package com.xuannguyen.identity.controller.order;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.order.OrderDetailRequest;
import com.xuannguyen.identity.entity.OrderDetail;
import com.xuannguyen.identity.service.orderservice.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    /**
     * Tạo mới chi tiết đơn hàng.
     *
     * Body:
     * {
     *   "orderid": "order-id",
     *   "productId": "product-id",
     *   "productQuantity": 2
     * }
     */
    @PostMapping
    public ApiResponse<OrderDetail> createOrderDetail(
            @RequestBody OrderDetailRequest request
    ) {
        return ApiResponse.<OrderDetail>builder()
                .message("Tạo chi tiết đơn hàng thành công.")
                .result(orderDetailService.createOrderDetail(request))
                .build();
    }

    /**
     * Cập nhật chi tiết đơn hàng.
     */
    @PutMapping("/{id}")
    public ApiResponse<OrderDetail> updateOrderDetail(
            @PathVariable String id,
            @RequestBody OrderDetailRequest request
    ) {
        return ApiResponse.<OrderDetail>builder()
                .message("Cập nhật chi tiết đơn hàng thành công.")
                .result(orderDetailService.updateOrderDetail(id, request))
                .build();
    }

    /**
     * Xóa chi tiết đơn hàng.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrderDetail(
            @PathVariable String id
    ) {
        orderDetailService.deleteOrderDetail(id);

        return ApiResponse.<Void>builder()
                .message("Xóa chi tiết đơn hàng thành công.")
                .build();
    }

    /**
     * Lấy tất cả chi tiết đơn hàng.
     */
    @GetMapping
    public ApiResponse<List<OrderDetail>> getAllOrderDetail() {
        return ApiResponse.<List<OrderDetail>>builder()
                .message("Lấy danh sách chi tiết đơn hàng thành công.")
                .result(orderDetailService.getAllOrderDetail())
                .build();
    }

    /**
     * Lấy chi tiết đơn hàng theo id.
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderDetail> getOrderDetailById(
            @PathVariable String id
    ) {
        return ApiResponse.<OrderDetail>builder()
                .message("Lấy chi tiết đơn hàng thành công.")
                .result(orderDetailService.getOrderDetailById(id))
                .build();
    }

    /**
     * Lấy danh sách chi tiết đơn hàng theo orderId.
     */
    @GetMapping("/order/{orderId}")
    public ApiResponse<List<OrderDetail>> getOrderDetailByOrderId(
            @PathVariable String orderId
    ) {
        return ApiResponse.<List<OrderDetail>>builder()
                .message("Lấy danh sách chi tiết đơn hàng theo đơn hàng thành công.")
                .result(orderDetailService.getOrderDetailByOrderId(orderId))
                .build();
    }

    /**
     * Thêm sản phẩm vào order dạng giỏ hàng PENDING.
     *
     * Nếu user chưa có order PENDING thì tạo mới.
     * Nếu sản phẩm đã tồn tại trong order PENDING thì cộng thêm số lượng.
     */
    @PostMapping("/cart/users/{userId}")
    public ApiResponse<OrderDetail> addToCart(
            @PathVariable Long userId,
            @RequestBody OrderDetailRequest request
    ) {
        return ApiResponse.<OrderDetail>builder()
                .message("Thêm sản phẩm vào giỏ hàng thành công.")
                .result(orderDetailService.addToCart(userId, request))
                .build();
    }
}