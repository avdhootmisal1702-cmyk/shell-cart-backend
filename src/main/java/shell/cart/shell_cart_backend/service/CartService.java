package shell.cart.shell_cart_backend.service;

import org.springframework.stereotype.Service;
import shell.cart.shell_cart_backend.entity.CartItem;
import shell.cart.shell_cart_backend.entity.Product;
import shell.cart.shell_cart_backend.entity.User;
import shell.cart.shell_cart_backend.exception.BadRequestException;
import shell.cart.shell_cart_backend.exception.ForbiddenException;
import shell.cart.shell_cart_backend.exception.ResourceNotFoundException;
import shell.cart.shell_cart_backend.repository.CartItemRepository;
import shell.cart.shell_cart_backend.repository.ProductRepository;
import shell.cart.shell_cart_backend.repository.UserRepository;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    public CartItem addToCartByEmail(
            String email,
            Long productId,
            Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new BadRequestException(
                    "Quantity must be greater than zero"
            );
        }

        User user = getUserByEmail(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found: " + productId
                        ));

        if (product.getQuantity() <= 0) {
            throw new BadRequestException(
                    "Product is out of stock"
            );
        }

        CartItem existingItem =
                cartItemRepository
                        .findByUserIdAndProductId(
                                user.getId(),
                                productId
                        )
                        .orElse(null);

        if (existingItem != null) {

            int newQuantity =
                    existingItem.getQuantity() + quantity;

            if (newQuantity > product.getQuantity()) {
                throw new BadRequestException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            existingItem.setQuantity(newQuantity);

            return cartItemRepository.save(existingItem);
        }

        if (quantity > product.getQuantity()) {
            throw new BadRequestException(
                    "Insufficient stock for product: "
                            + product.getName()
            );
        }

        CartItem cartItem =
                new CartItem(
                        user.getId(),
                        productId,
                        quantity
                );

        return cartItemRepository.save(cartItem);
    }

    public List<CartItem> getCartByEmail(String email) {

        User user = getUserByEmail(email);

        return cartItemRepository.findByUserId(user.getId());
    }

    public CartItem updateQuantity(
            String email,
            Long cartItemId,
            Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new BadRequestException(
                    "Quantity must be greater than zero"
            );
        }

        User user = getUserByEmail(email);

        CartItem cartItem =
                cartItemRepository.findById(cartItemId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart item not found: "
                                                + cartItemId
                                ));

        if (!cartItem.getUserId().equals(user.getId())) {
            throw new ForbiddenException(
                    "You are not allowed to modify this cart item"
            );
        }

        Product product =
                productRepository.findById(
                        cartItem.getProductId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found: "
                                        + cartItem.getProductId()
                        ));

        if (quantity > product.getQuantity()) {
            throw new BadRequestException(
                    "Insufficient stock for product: "
                            + product.getName()
            );
        }

        cartItem.setQuantity(quantity);

        return cartItemRepository.save(cartItem);
    }

    public void removeFromCart(
            String email,
            Long cartItemId) {

        User user = getUserByEmail(email);

        CartItem cartItem =
                cartItemRepository.findById(cartItemId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart item not found: "
                                                + cartItemId
                                ));

        if (!cartItem.getUserId().equals(user.getId())) {
            throw new ForbiddenException(
                    "You are not allowed to remove this cart item"
            );
        }

        cartItemRepository.delete(cartItem);
    }

    public void clearCart(String email) {

        User user = getUserByEmail(email);

        List<CartItem> cartItems =
                cartItemRepository.findByUserId(user.getId());

        cartItemRepository.deleteAll(cartItems);
    }
}