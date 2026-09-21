package shell.cart.shell_cart_backend.dto;

import shell.cart.shell_cart_backend.entity.CartItem;

public class CartItemResponse {

    private Long id;
    private Long productId;
    private Integer quantity;

    public CartItemResponse() {
    }

    public CartItemResponse(
            Long id,
            Long productId,
            Integer quantity) {

        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public static CartItemResponse fromCartItem(
            CartItem cartItem) {

        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProductId(),
                cartItem.getQuantity()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}