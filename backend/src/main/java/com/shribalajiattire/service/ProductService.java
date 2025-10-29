package com.shribalajiattire.service;

import com.shribalajiattire.dto.CreateProductRequest;
import com.shribalajiattire.dto.ProductDTO;
import com.shribalajiattire.model.Product;
import com.shribalajiattire.model.ProductImage;
import com.shribalajiattire.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    
    public Page<ProductDTO> getProducts(String query, List<String> sizes, List<String> colors,
                                        Double minPrice, Double maxPrice, String sort,
                                        int page, int size) {
        Sort sortOrder = getSortOrder(sort);
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        
        Specification<Product> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            predicates.add(criteriaBuilder.isTrue(root.get("active")));
            
            if (query != null && !query.isEmpty()) {
                String likePattern = "%" + query.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), likePattern);
                Predicate descPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), likePattern);
                predicates.add(criteriaBuilder.or(namePredicate, descPredicate));
            }
            
            if (sizes != null && !sizes.isEmpty()) {
                predicates.add(root.join("sizes").in(sizes));
            }
            
            if (colors != null && !colors.isEmpty()) {
                predicates.add(root.join("colors").in(colors));
            }
            
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("priceCents"), (long) (minPrice * 100)));
            }
            
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("priceCents"), (long) (maxPrice * 100)));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return productRepository.findAll(spec, pageable).map(ProductDTO::fromProduct);
    }
    
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductDTO.fromProduct(product);
    }
    
    public ProductDTO getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductDTO.fromProduct(product);
    }
    
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request, List<MultipartFile> images) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("SKU already exists");
        }
        
        String slug = generateSlug(request.getName());
        
        Product product = Product.builder()
                .name(request.getName())
                .slug(slug)
                .sku(request.getSku())
                .description(request.getDescription())
                .priceCents((long) (request.getPrice() * 100))
                .currency("INR")
                .sizes(request.getSizes() != null ? request.getSizes() : new ArrayList<>())
                .colors(request.getColors() != null ? request.getColors() : new ArrayList<>())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .active(true)
                .build();
        
        product = productRepository.save(product);
        
        if (images != null && !images.isEmpty()) {
            int order = 0;
            for (MultipartFile image : images) {
                String imageUrl = fileStorageService.storeFile(image);
                ProductImage productImage = ProductImage.builder()
                        .url(imageUrl)
                        .altText(product.getName())
                        .displayOrder(order++)
                        .build();
                product.addImage(productImage);
            }
            product = productRepository.save(product);
        }
        
        return ProductDTO.fromProduct(product);
    }
    
    @Transactional
    public ProductDTO updateProduct(Long id, CreateProductRequest request, List<MultipartFile> images) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPriceCents((long) (request.getPrice() * 100));
        
        if (request.getSizes() != null) {
            product.setSizes(request.getSizes());
        }
        if (request.getColors() != null) {
            product.setColors(request.getColors());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        
        if (images != null && !images.isEmpty()) {
            int order = product.getImages().size();
            for (MultipartFile image : images) {
                String imageUrl = fileStorageService.storeFile(image);
                ProductImage productImage = ProductImage.builder()
                        .url(imageUrl)
                        .altText(product.getName())
                        .displayOrder(order++)
                        .build();
                product.addImage(productImage);
            }
        }
        
        product = productRepository.save(product);
        return ProductDTO.fromProduct(product);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }
    
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }
    
    private Sort getSortOrder(String sort) {
        if (sort == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        
        return switch (sort) {
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "priceCents");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "priceCents");
            case "name-asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name-desc" -> Sort.by(Sort.Direction.DESC, "name");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}
