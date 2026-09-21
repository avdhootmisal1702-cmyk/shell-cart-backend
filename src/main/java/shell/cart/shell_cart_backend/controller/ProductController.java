package shell.cart.shell_cart_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import shell.cart.shell_cart_backend.dto.ProductRequest;
import shell.cart.shell_cart_backend.dto.ProductResponse;
import shell.cart.shell_cart_backend.entity.Product;
import shell.cart.shell_cart_backend.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(
        name = "Products",
        description = "Product management APIs"
)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Create product",
            description = "Creates a new product. Admin access required."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            )
    })
    @PostMapping
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request) {

        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getQuantity(),
                request.getCategory(),
                request.getImageUrl()
        );

        return ProductResponse.fromProduct(
                productService.createProduct(product)
        );
    }

    @Operation(
            summary = "Get all products",
            description = "Returns all products."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @GetMapping
    public List<ProductResponse> getAllProducts() {

        return productService.getAllProducts()
                .stream()
                .map(ProductResponse::fromProduct)
                .toList();
    }

    @Operation(
            summary = "Get product by ID",
            description = "Returns a single product using its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @GetMapping("/{id}")
    public ProductResponse getProductById(
            @PathVariable Long id) {

        return ProductResponse.fromProduct(
                productService.getProductById(id)
        );
    }

    @Operation(
            summary = "Update product",
            description = "Updates an existing product. Admin access required."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getQuantity(),
                request.getCategory(),
                request.getImageUrl()
        );

        return ProductResponse.fromProduct(
                productService.updateProduct(id, product)
        );
    }

    @Operation(
            summary = "Delete product",
            description = "Deletes a product using its ID. Admin access required."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @DeleteMapping("/{id}")
    public String deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return "Product deleted successfully";
    }
}