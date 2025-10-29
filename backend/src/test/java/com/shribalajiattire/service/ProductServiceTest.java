package com.shribalajiattire.service;

import com.shribalajiattire.dto.ProductDTO;
import com.shribalajiattire.model.Product;
import com.shribalajiattire.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private FileStorageService fileStorageService;
    
    @InjectMocks
    private ProductService productService;
    
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Shirt")
                .slug("test-shirt")
                .sku("TEST-001")
                .description("A test shirt")
                .priceCents(199900L)
                .currency("INR")
                .sizes(new ArrayList<>())
                .colors(new ArrayList<>())
                .images(new ArrayList<>())
                .stock(10)
                .active(true)
                .build();
    }
    
    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        ProductDTO result = productService.getProductById(1L);
        
        assertNotNull(result);
        assertEquals("Test Shirt", result.getName());
        assertEquals("TEST-001", result.getSku());
        assertEquals(1999.0, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
    }
    
    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> productService.getProductById(999L));
        verify(productRepository, times(1)).findById(999L);
    }
    
    @Test
    void getProductBySlug_ShouldReturnProduct_WhenSlugExists() {
        when(productRepository.findBySlug("test-shirt")).thenReturn(Optional.of(testProduct));
        
        ProductDTO result = productService.getProductBySlug("test-shirt");
        
        assertNotNull(result);
        assertEquals("test-shirt", result.getSlug());
        verify(productRepository, times(1)).findBySlug("test-shirt");
    }
}
