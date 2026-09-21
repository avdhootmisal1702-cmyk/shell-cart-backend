package shell.cart.shell_cart_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shell.cart.shell_cart_backend.entity.User;
import shell.cart.shell_cart_backend.exception.ResourceNotFoundException;
import shell.cart.shell_cart_backend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {

        String hashedPassword =
                passwordEncoder.encode(user.getPassword());

        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {

        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "User not found: " + id
            );
        }

        userRepository.deleteById(id);
    }

    public String generatePasswordHash(String password) {

        return passwordEncoder.encode(password);
    }
}