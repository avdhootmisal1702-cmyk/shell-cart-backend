package shell.cart.shell_cart_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shell.cart.shell_cart_backend.entity.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndProductId(
            Long userId,
            Long productId
    );
}