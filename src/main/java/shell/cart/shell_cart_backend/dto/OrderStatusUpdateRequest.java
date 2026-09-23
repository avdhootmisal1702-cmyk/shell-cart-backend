package shell.cart.shell_cart_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OrderStatusUpdateRequest {

    @NotBlank(message = "Order status is required")
    @Pattern(
            regexp = "PLACED|PROCESSING|SHIPPED|DELIVERED|CANCELLED",
            message = "Invalid order status"
    )
    private String status;

    public OrderStatusUpdateRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}