package shell.cart.shell_cart_backend.dto;

import shell.cart.shell_cart_backend.entity.CartItem;

public class CartItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String imageUrl;
    private String price;
    private Integer quantity;

    public CartItemResponse() {
    }

    public CartItemResponse(
            Long id,
            Long productId,
            String productName,
            String imageUrl,
            String price,
            Integer quantity) {

        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public static CartItemResponse fromCartItem(
            CartItem cartItem,
            String productName,
            String imageUrl,
            String price) {

        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProductId(),
                productName,
                imageUrl,
                price,
                cartItem.getQuantity()
        );
    }
}