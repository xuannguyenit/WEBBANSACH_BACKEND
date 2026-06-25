package com.xuannguyen.identity.service.cartservice;

import com.xuannguyen.identity.dto.request.cartitem.UpdateCartItemRequest;
import com.xuannguyen.identity.dto.response.cartitem.CartItemResponse;

public interface CartItemService {
    CartItemResponse addToCart(String productId);
    void removeFromCart(String cartId, String productId);
    CartItemResponse updateCart(UpdateCartItemRequest request);
    void deleteCart(String cartId);
}
