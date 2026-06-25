package com.xuannguyen.identity.service.cartservice.impl;

import com.xuannguyen.identity.dto.request.cartitem.UpdateCartItemRequest;
import com.xuannguyen.identity.dto.response.cartitem.CartItemResponse;
import com.xuannguyen.identity.entity.*;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.*;
import com.xuannguyen.identity.service.cartservice.CartItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String userName = authentication.getName();

        if (userName == null || userName.trim().isEmpty()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @Override
    @Transactional
    public CartItemResponse addToCart(String productId) {
        User user = getCurrentUser();

        Product product = findProductById(productId);

        validateProductCanAddToCart(product);

        Cart cart = getOrCreateCart(user);

        Long discountPercentage = getDiscountPercentage(product);

        BigDecimal priceAfterDiscount = calculatePriceAfterDiscount(
                product.getPrice(),
                discountPercentage
        );

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (cartItem != null) {
            int updatedQuantity = cartItem.getProductQuantity() + 1;

            if (updatedQuantity > product.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY_OF_PRODUCTS);
            }

            cartItem.setProductQuantity(updatedQuantity);
            cartItem.setProductPrice(priceAfterDiscount);
            cartItem.setDiscountPercentage(discountPercentage);
            cartItem.setTotalPrice(
                    priceAfterDiscount.multiply(BigDecimal.valueOf(updatedQuantity))
            );
        } else {
            cartItem = new CartItem();
            cartItem.setProductId(product.getId());
            cartItem.setProductName(product.getName());
            cartItem.setCart(cart);
            cartItem.setUserId(user.getId());
            cartItem.setProductPrice(priceAfterDiscount);
            cartItem.setProductQuantity(1);
            cartItem.setDiscountPercentage(discountPercentage);
            cartItem.setTotalPrice(priceAfterDiscount);
        }

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        recalculateCartTotalPrice(cart);

        return toCartItemResponse(savedCartItem);
    }

    @Override
    @Transactional
    public CartItemResponse updateCart(UpdateCartItemRequest request) {
        User user = getCurrentUser();

        if (request == null) {
            throw new AppException(ErrorCode.UPDATE_CART_ITEM_REQUEST_INVALID);
        }

        if (isBlank(request.getCartId())) {
            throw new AppException(ErrorCode.CART_ID_REQUIRED);
        }

        if (isBlank(request.getProductId())) {
            throw new AppException(ErrorCode.PRODUCT_ID_REQUIRED);
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new AppException(ErrorCode.INVALID_CART_ITEM_QUANTITY);
        }

        Cart cart = findCartAndValidateOwner(request.getCartId(), user);

        Product product = findProductById(request.getProductId());

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_EXISTED));

        validateProductCanAddToCart(product);

        if (request.getQuantity() > product.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY_OF_PRODUCTS);
        }

        Long discountPercentage = getDiscountPercentage(product);

        BigDecimal priceAfterDiscount = calculatePriceAfterDiscount(
                product.getPrice(),
                discountPercentage
        );

        cartItem.setProductName(product.getName());
        cartItem.setProductPrice(priceAfterDiscount);
        cartItem.setProductQuantity(request.getQuantity());
        cartItem.setDiscountPercentage(discountPercentage);
        cartItem.setTotalPrice(
                priceAfterDiscount.multiply(BigDecimal.valueOf(request.getQuantity()))
        );
        cartItem.setUserId(user.getId());

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        recalculateCartTotalPrice(cart);

        return toCartItemResponse(savedCartItem);
    }

    @Override
    @Transactional
    public void removeFromCart(String cartId, String productId) {
        User user = getCurrentUser();

        if (isBlank(cartId)) {
            throw new AppException(ErrorCode.CART_ID_REQUIRED);
        }

        if (isBlank(productId)) {
            throw new AppException(ErrorCode.PRODUCT_ID_REQUIRED);
        }

        Cart cart = findCartAndValidateOwner(cartId, user);

        Product product = findProductById(productId);

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_EXISTED));

        cartItemRepository.delete(cartItem);

        recalculateCartTotalPrice(cart);
    }

    @Override
    @Transactional
    public void deleteCart(String cartId) {
        User user = getCurrentUser();

        if (isBlank(cartId)) {
            throw new AppException(ErrorCode.CART_ID_REQUIRED);
        }

        Cart cart = findCartAndValidateOwner(cartId, user);

        cartItemRepository.deleteAllByCartId(cart.getId());

        cart.setTotalPrice(BigDecimal.ZERO);

        cartRepository.save(cart);
    }

    private Product findProductById(String productId) {
        if (isBlank(productId)) {
            throw new AppException(ErrorCode.PRODUCT_ID_REQUIRED);
        }

        return productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setTotalPrice(BigDecimal.ZERO);
                    newCart.setUserId(user.getId());
                    return cartRepository.save(newCart);
                });
    }

    private Cart findCartAndValidateOwner(String cartId, User user) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        if (!Objects.equals(cart.getUserId(), user.getId())) {
            throw new AppException(ErrorCode.CART_NOT_BELONG_TO_USER);
        }

        return cart;
    }

    private void validateProductCanAddToCart(Product product) {
        if (product.getEnable() != null && !product.getEnable()) {
            throw new AppException(ErrorCode.PRODUCT_DISABLED);
        }

        if (product.getQuantity() <= 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY_OF_PRODUCTS);
        }
    }

    private Long getDiscountPercentage(Product product) {
        if (product == null || product.getDiscountCode() == null) {
            return 0L;
        }

        Long discountPercentage = product.getDiscountCode().getDiscountPercentage();

        if (discountPercentage == null || discountPercentage < 0) {
            return 0L;
        }

        if (discountPercentage > 100) {
            return 100L;
        }

        return discountPercentage;
    }

    private BigDecimal calculatePriceAfterDiscount(BigDecimal price, Long discountPercentage) {
        if (price == null) {
            return BigDecimal.ZERO;
        }

        if (discountPercentage == null || discountPercentage <= 0) {
            return price;
        }

        BigDecimal discountRate = BigDecimal.valueOf(discountPercentage)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        return price.subtract(price.multiply(discountRate))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void recalculateCartTotalPrice(Cart cart) {
        BigDecimal totalPrice = cartItemRepository.findAllByCartId(cart.getId())
                .stream()
                .map(CartItem::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(totalPrice);

        cartRepository.save(cart);
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .cartId(cartItem.getCart() != null ? cartItem.getCart().getId() : null)
                .productId(cartItem.getProductId())
                .productName(cartItem.getProductName())
                .productPrice(cartItem.getProductPrice())
                .discountPercentage(cartItem.getDiscountPercentage())
                .productQuantity(cartItem.getProductQuantity())
                .totalPrice(cartItem.getTotalPrice())
                .userId(cartItem.getUserId())
                .build();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

