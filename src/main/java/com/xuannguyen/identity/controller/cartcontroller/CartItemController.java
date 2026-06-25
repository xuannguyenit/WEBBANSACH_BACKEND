package com.xuannguyen.identity.controller.cartcontroller;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.cartitem.UpdateCartItemRequest;
import com.xuannguyen.identity.dto.response.cartitem.CartItemResponse;
import com.xuannguyen.identity.service.cartservice.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping("/add/{productId}")
    public ApiResponse<CartItemResponse> addToCart(@PathVariable String productId) {
        return ApiResponse.<CartItemResponse>builder()
                .message("Thêm sản phẩm vào giỏ hàng thành công.")
                .result(cartItemService.addToCart(productId))
                .build();
    }

    @PutMapping
    public ApiResponse<CartItemResponse> updateCart(
            @RequestBody UpdateCartItemRequest request
    ) {
        return ApiResponse.<CartItemResponse>builder()
                .message("Cập nhật sản phẩm trong giỏ hàng thành công.")
                .result(cartItemService.updateCart(request))
                .build();
    }

    @DeleteMapping("/{cartId}/products/{productId}")
    public ApiResponse<Void> removeFromCart(
            @PathVariable String cartId,
            @PathVariable String productId
    ) {
        cartItemService.removeFromCart(cartId, productId);

        return ApiResponse.<Void>builder()
                .message("Xóa sản phẩm khỏi giỏ hàng thành công.")
                .build();
    }

    @DeleteMapping("/cart/{cartId}")
    public ApiResponse<Void> deleteCart(@PathVariable String cartId) {
        cartItemService.deleteCart(cartId);

        return ApiResponse.<Void>builder()
                .message("Xóa toàn bộ giỏ hàng thành công.")
                .build();
    }
}
