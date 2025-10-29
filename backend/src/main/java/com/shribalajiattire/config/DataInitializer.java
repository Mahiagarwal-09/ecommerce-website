package com.shribalajiattire.config;

import com.shribalajiattire.model.Product;
import com.shribalajiattire.model.ProductImage;
import com.shribalajiattire.model.User;
import com.shribalajiattire.repository.ProductRepository;
import com.shribalajiattire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (userRepository.count() == 0) {
                createUsers();
            }
            
            if (productRepository.count() == 0) {
                createProducts();
            }
        };
    }
    
    private void createUsers() {
        User admin = User.builder()
                .name("Admin User")
                .email("admin@shribalajiattire.com")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .build();
        
        User customer = User.builder()
                .name("Test Customer")
                .email("customer@test.com")
                .password(passwordEncoder.encode("customer123"))
                .role(User.Role.CUSTOMER)
                .build();
        
        userRepository.saveAll(Arrays.asList(admin, customer));
        log.info("Created default users - Admin: admin@shribalajiattire.com / admin123");
    }
    
    private void createProducts() {
        String[] productNames = {
            "Classic White Formal Shirt",
            "Blue Oxford Business Shirt",
            "Black Slim Fit Shirt",
            "Pink Casual Shirt",
            "Navy Blue Formal Shirt",
            "Grey Checkered Shirt",
            "Light Blue Linen Shirt",
            "Burgundy Party Shirt",
            "Mint Green Casual Shirt",
            "Charcoal Formal Shirt"
        };
        
        String[] descriptions = {
            "Premium cotton formal shirt perfect for office wear. Features a classic collar and comfortable fit.",
            "Oxford weave business shirt with button-down collar. Ideal for professional settings.",
            "Modern slim fit design in pure black. Perfect for formal events and business meetings.",
            "Comfortable casual shirt in soft pink. Great for weekend outings and casual Fridays.",
            "Traditional navy blue formal shirt. A wardrobe essential for every professional.",
            "Stylish grey checkered pattern. Versatile enough for both casual and semi-formal occasions.",
            "Breathable linen fabric in light blue. Perfect for summer and tropical climates.",
            "Rich burgundy color for special occasions. Stand out at parties and celebrations.",
            "Fresh mint green casual shirt. Modern color for the fashion-forward individual.",
            "Sophisticated charcoal grey formal shirt. Timeless elegance for the modern gentleman."
        };
        
        String[] colors = {"White", "Blue", "Black", "Pink", "Navy", "Grey", "Light Blue", "Burgundy", "Mint", "Charcoal"};
        
        List<String> sizes = Arrays.asList("S", "M", "L", "XL", "XXL");
        
        for (int i = 0; i < productNames.length; i++) {
            Product product = Product.builder()
                    .name(productNames[i])
                    .slug(generateSlug(productNames[i]))
                    .sku("SBA-" + String.format("%04d", i + 1))
                    .description(descriptions[i])
                    .priceCents((long) ((1499 + (i * 200)) * 100))
                    .currency("INR")
                    .sizes(sizes)
                    .colors(Arrays.asList(colors[i]))
                    .stock(50 + (i * 5))
                    .active(true)
                    .build();
            
            // Add placeholder image
            ProductImage image = ProductImage.builder()
                    .url("https://via.placeholder.com/600x800/4A5568/FFFFFF?text=" + productNames[i].replaceAll(" ", "+"))
                    .altText(productNames[i])
                    .displayOrder(0)
                    .build();
            
            product.addImage(image);
            productRepository.save(product);
        }
        
        log.info("Created {} sample products", productNames.length);
    }
    
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }
}
