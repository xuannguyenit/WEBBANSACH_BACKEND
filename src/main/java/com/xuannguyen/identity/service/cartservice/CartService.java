package com.xuannguyen.identity.service.cartservice;

import com.xuannguyen.identity.entity.CartItem;

import java.util.List;

public interface CartService {
    List<CartItem> getCartItems(String cartId);
    List<CartItem> getCartByUserId(Long userId);
    void deleteCart(String cartId);
}
