package shell.cart.shell_cart_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shell.cart.shell_cart_backend.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}