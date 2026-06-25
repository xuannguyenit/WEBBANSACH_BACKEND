package com.xuannguyen.identity.repository;
import com.xuannguyen.identity.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface CartItemRepository extends JpaRepository<CartItem,String> {
    // Lấy danh sách CartItem theo cartId
    List<CartItem> findAllByCartId(String cartId);
    //lấy danh sách sản phẩm mua theo userId
    List<CartItem> findAllByUserId(Long userId);
    // Tìm CartItem theo cartId và productId
    Optional<CartItem> findByCartIdAndProductId(String cartId, String productId);
    void deleteAllByCartId(String cartId);
}
