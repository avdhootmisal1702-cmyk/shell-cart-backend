package shell.cart.shell_cart_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shell.cart.shell_cart_backend.entity.CartItem;
import shell.cart.shell_cart_backend.entity.Order;
import shell.cart.shell_cart_backend.entity.OrderItem;
import shell.cart.shell_cart_backend.entity.Product;
import shell.cart.shell_cart_backend.entity.User;
import shell.cart.shell_cart_backend.exception.BadRequestException;
import shell.cart.shell_cart_backend.exception.ForbiddenException;
import shell.cart.shell_cart_backend.exception.ResourceNotFoundException;
import shell.cart.shell_cart_backend.repository.CartItemRepository;
import shell.cart.shell_cart_backend.repository.OrderItemRepository;
import shell.cart.shell_cart_backend.repository.OrderRepository;
import shell.cart.shell_cart_backend.repository.ProductRepository;
import shell.cart.shell_cart_backend.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order createOrderFromEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        return createOrderFromCart(user.getId());
    }

    @Transactional
    public Order createOrderFromCart(Long userId) {

        List<CartItem> cartItems =
                cartItemRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new BadRequestException(
                    "Cart is empty"
            );
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Check stock and calculate total
        for (CartItem cartItem : cartItems) {

            Product product =
                    productRepository.findByIdForUpdate(
                            cartItem.getProductId()
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found: "
                                            + cartItem.getProductId()
                            ));

            if (cartItem.getQuantity() <= 0) {
                throw new BadRequestException(
                        "Cart quantity must be greater than zero"
                );
            }

            if (product.getQuantity()
                    < cartItem.getQuantity()) {

                throw new BadRequestException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            BigDecimal itemTotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );

            totalAmount =
                    totalAmount.add(itemTotal);
        }

        // Create order
        Order order =
                new Order(
                        userId,
                        totalAmount,
                        "PLACED",
                        LocalDateTime.now()
                );

        Order savedOrder =
                orderRepository.save(order);

        // Create order items and reduce stock
        for (CartItem cartItem : cartItems) {

            Product product =
                    productRepository.findByIdForUpdate(
                            cartItem.getProductId()
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found: "
                                            + cartItem.getProductId()
                            ));

            OrderItem orderItem =
                    new OrderItem(
                            savedOrder.getId(),
                            product.getId(),
                            cartItem.getQuantity(),
                            product.getPrice()
                    );

            orderItemRepository.save(orderItem);

            product.setQuantity(
                    product.getQuantity()
                            - cartItem.getQuantity()
            );

            productRepository.save(product);
        }

        // Clear cart
        cartItemRepository.deleteAll(cartItems);

        return savedOrder;
    }

    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }

    // Used by admin to view any order
    public Order getOrderById(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found: "
                                        + orderId
                        ));
    }

    // Used by admin to view items of any order
    public List<OrderItem> getOrderItems(Long orderId) {

        // Make sure the order exists
        orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found: "
                                        + orderId
                        ));

        return orderItemRepository.findByOrderId(
                orderId
        );
    }

    // Used by admin to update order status
    public Order updateOrderStatus(
            Long orderId,
            String status) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found: "
                                                + orderId
                                ));

        order.setStatus(status);

        return orderRepository.save(order);
    }

    public List<Order> getUserOrdersByEmail(
            String email) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                ));

        return orderRepository.findByUserId(
                user.getId()
        );
    }

    public Order getOrderByIdForUser(
            String email,
            Long orderId) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                ));

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found: "
                                                + orderId
                                ));

        if (!order.getUserId()
                .equals(user.getId())) {

            throw new ForbiddenException(
                    "You are not allowed to access this order"
            );
        }

        return order;
    }

    public List<OrderItem> getOrderItemsForUser(
            String email,
            Long orderId) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                ));

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found: "
                                                + orderId
                                ));

        if (!order.getUserId()
                .equals(user.getId())) {

            throw new ForbiddenException(
                    "You are not allowed to access this order"
            );
        }

        return orderItemRepository.findByOrderId(
                orderId
        );
    }
}