package com.xuannguyen.identity.service.cartservice.impl;

import com.xuannguyen.identity.entity.Cart;
import com.xuannguyen.identity.entity.CartItem;
import com.xuannguyen.identity.repository.CartItemRepository;
import com.xuannguyen.identity.repository.CartRepository;
import com.xuannguyen.identity.service.cartservice.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;


    /**
     * Lấy danh sách CartItem trong giỏ hàng
     */
    @Override
    public List<CartItem> getCartItems(String cartId) {
        return cartItemRepository.findAllByCartId(cartId);
    }

    @Override
    public List<CartItem> getCartByUserId(Long userId) {
        return cartItemRepository.findAllByUserId(userId);
    }

    @Override
    public void deleteCart(String cartId) {

        if (!cartRepository.existsById(cartId)) {
            return;
        }
        cartRepository.deleteById(cartId);

    }

    /**
     * Xóa giỏ hàng và toàn bộ CartItem liên quan
     */
    public void clearCart(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cartRepository.delete(cart);
    }
}
