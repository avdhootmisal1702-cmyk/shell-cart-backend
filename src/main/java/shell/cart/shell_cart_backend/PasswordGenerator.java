package shell.cart.shell_cart_backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String password = "test123";

        String hash = encoder.encode(password);

        System.out.println("BCrypt Hash:");
        System.out.println(hash);
    }
}