package shell.cart.shell_cart_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shell.cart.shell_cart_backend.dto.CartItemResponse;
import shell.cart.shell_cart_backend.entity.CartItem;
import shell.cart.shell_cart_backend.service.CartService;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Validated
@Tag(
        name = "Cart",
        description = "Shopping cart management APIs"
)
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(
            summary = "Add product to cart",
            description = "Adds a product to the logged-in user's cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product added to cart successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid quantity, product out of stock, or insufficient stock"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product or user not found"
            )
    })
    @PostMapping
    public CartItemResponse addToCart(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam
            @Min(
                    value = 1,
                    message = "Quantity must be at least 1"
            )
            Integer quantity) {

        String email = authentication.getName();

        CartItem cartItem = cartService.addToCartByEmail(
                email,
                productId,
                quantity
        );

        return CartItemResponse.fromCartItem(cartItem);
    }

    @Operation(
            summary = "Get my cart",
            description = "Returns all cart items belonging to the logged-in user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @GetMapping
    public List<CartItemResponse> getCart(
            Authentication authentication) {

        String email = authentication.getName();

        return cartService.getCartByEmail(email)
                .stream()
                .map(CartItemResponse::fromCartItem)
                .toList();
    }

    @Operation(
            summary = "Update cart item quantity",
            description = "Updates the quantity of a product in the logged-in user's cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart quantity updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid quantity or insufficient stock"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Cart item belongs to another user"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart item or product not found"
            )
    })
    @PutMapping("/{cartItemId}")
    public CartItemResponse updateQuantity(
            Authentication authentication,
            @PathVariable Long cartItemId,
            @RequestParam
            @Min(
                    value = 1,
                    message = "Quantity must be at least 1"
            )
            Integer quantity) {

        String email = authentication.getName();

        CartItem cartItem = cartService.updateQuantity(
                email,
                cartItemId,
                quantity
        );

        return CartItemResponse.fromCartItem(cartItem);
    }

    @Operation(
            summary = "Remove cart item",
            description = "Removes a specific item from the logged-in user's cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart item removed successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Cart item belongs to another user"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart item not found"
            )
    })
    @DeleteMapping("/{cartItemId}")
    public String removeFromCart(
            Authentication authentication,
            @PathVariable Long cartItemId) {

        String email = authentication.getName();

        cartService.removeFromCart(
                email,
                cartItemId
        );

        return "Cart item removed successfully";
    }

    @Operation(
            summary = "Clear cart",
            description = "Removes all items from the logged-in user's cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart cleared successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @DeleteMapping("/clear")
    public String clearCart(
            Authentication authentication) {

        String email = authentication.getName();

        cartService.clearCart(email);

        return "Cart cleared successfully";
    }
}