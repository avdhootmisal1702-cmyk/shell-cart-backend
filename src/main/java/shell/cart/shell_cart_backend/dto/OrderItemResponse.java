package shell.cart.shell_cart_backend.dto;

import shell.cart.shell_cart_backend.entity.OrderItem;

import java.math.BigDecimal;

public class OrderItemResponse {

    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;

    public OrderItemResponse() {
    }

    public OrderItemResponse(
            Long id,
            Long productId,
            Integer quantity,
            BigDecimal price) {

        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderItemResponse fromOrderItem(OrderItem orderItem) {

        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getPrice()
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

    public BigDecimal getPrice() {
        return price;
    }
}