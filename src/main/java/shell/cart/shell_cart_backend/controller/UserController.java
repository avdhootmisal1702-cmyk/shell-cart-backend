package shell.cart.shell_cart_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import shell.cart.shell_cart_backend.dto.UserRegistrationRequest;
import shell.cart.shell_cart_backend.dto.UserResponse;
import shell.cart.shell_cart_backend.entity.User;
import shell.cart.shell_cart_backend.exception.ForbiddenException;
import shell.cart.shell_cart_backend.exception.ResourceNotFoundException;
import shell.cart.shell_cart_backend.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(
        name = "Users",
        description = "User registration and user management APIs"
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Register user",
            description = "Creates a new user account. New users are automatically assigned the USER role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data"
            )
    })
    @PostMapping
    public UserResponse createUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                "USER"
        );

        User savedUser = userService.createUser(user);

        return UserResponse.fromUser(savedUser);
    }

    @Operation(
            summary = "Get all users",
            description = "Returns all registered users. Admin access required."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"
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
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers()
                .stream()
                .map(UserResponse::fromUser)
                .toList();
    }

    @Operation(
            summary = "Get user by ID",
            description = "Returns the requested user. A regular user can access only their own profile; admins can access any user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not allowed to access this profile"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/{id}")
    public UserResponse getUserById(
            Authentication authentication,
            @PathVariable Long id) {

        String email = authentication.getName();

        User loggedInUser = userService.getUserByEmail(email);

        if (!loggedInUser.getId().equals(id)
                && !loggedInUser.getRole().equalsIgnoreCase("ADMIN")) {

            throw new ForbiddenException(
                    "You are not allowed to access this user"
            );
        }

        User user = userService.getUserById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + id
                        ));

        return UserResponse.fromUser(user);
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user account. Admin access required."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User deleted successfully"
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
                    description = "User not found"
            )
    })
    @DeleteMapping("/{id}")
    public String deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return "User deleted successfully";
    }
}
