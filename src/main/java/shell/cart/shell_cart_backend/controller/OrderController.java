package shell.cart.shell_cart_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import shell.cart.shell_cart_backend.dto.OrderItemResponse;
import shell.cart.shell_cart_backend.dto.OrderResponse;
import shell.cart.shell_cart_backend.dto.OrderStatusUpdateRequest;
import shell.cart.shell_cart_backend.entity.Order;
import shell.cart.shell_cart_backend.entity.OrderItem;
import shell.cart.shell_cart_backend.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(
        name = "Orders",
        description = "Order and checkout management APIs"
)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Checkout",
            description = "Creates an order from the logged-in user's cart, reduces product stock, and clears the cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cart is empty or insufficient stock"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or product not found"
            )
    })
    @PostMapping("/checkout")
    public OrderResponse checkout(
            Authentication authentication) {

        String email = authentication.getName();

        Order order = orderService.createOrderFromEmail(email);

        return OrderResponse.fromOrder(order);
    }

    @Operation(
            summary = "Get all orders",
            description = "Returns all orders in the system. Admin access required."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            )
    })
    @GetMapping
    public List<OrderResponse> getAllOrders() {

        return orderService.getAllOrders()
                .stream()
                .map(OrderResponse::fromOrder)
                .toList();
    }

    @Operation(
            summary = "Get my orders",
            description = "Returns all orders belonging to the logged-in user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User orders retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @GetMapping("/my-orders")
    public List<OrderResponse> getMyOrders(
            Authentication authentication) {

        String email = authentication.getName();

        return orderService.getUserOrdersByEmail(email)
                .stream()
                .map(OrderResponse::fromOrder)
                .toList();
    }

    @Operation(
            summary = "Get order by ID",
            description = "Users can view their own orders. Admins can view any order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "You are not allowed to access this order"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    @GetMapping("/{id}")
    public OrderResponse getOrderById(
            Authentication authentication,
            @PathVariable Long id) {

        String email = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN")
                );

        Order order;

        if (isAdmin) {
            order = orderService.getOrderById(id);
        } else {
            order = orderService.getOrderByIdForUser(
                    email,
                    id
            );
        }

        return OrderResponse.fromOrder(order);
    }

    @Operation(
            summary = "Get order items",
            description = "Users can view items from their own orders. Admins can view items from any order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order items retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "You are not allowed to access this order"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    @GetMapping("/{orderId}/items")
    public List<OrderItemResponse> getOrderItems(
            Authentication authentication,
            @PathVariable Long orderId) {

        String email = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN")
                );

        List<OrderItem> orderItems;

        if (isAdmin) {
            orderItems = orderService.getOrderItems(orderId);
        } else {
            orderItems = orderService.getOrderItemsForUser(
                    email,
                    orderId
            );
        }

        return orderItems
                .stream()
                .map(OrderItemResponse::fromOrderItem)
                .toList();
    }

    @Operation(
            summary = "Update order status",
            description = "Allows an admin to update the status of an order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order status"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    @PutMapping("/{id}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {

        Order order = orderService.updateOrderStatus(
                id,
                request.getStatus()
        );

        return OrderResponse.fromOrder(order);
    }
}