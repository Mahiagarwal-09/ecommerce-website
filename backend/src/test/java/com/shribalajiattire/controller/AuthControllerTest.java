package com.shribalajiattire.controller;

import com.shribalajiattire.dto.AuthRequest;
import com.shribalajiattire.dto.AuthResponse;
import com.shribalajiattire.dto.RegisterRequest;
import com.shribalajiattire.dto.UserDTO;
import com.shribalajiattire.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthService authService;
    
    @Test
    void register_ShouldReturnAuthResponse_WhenValidRequest() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name("Test User")
                .email("test@test.com")
                .role("CUSTOMER")
                .build();
        
        AuthResponse authResponse = AuthResponse.builder()
                .token("test-token")
                .user(userDTO)
                .build();
        
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);
        
        String requestBody = """
                {
                    "name": "Test User",
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;
        
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.user.email").value("test@test.com"));
    }
    
    @Test
    void login_ShouldReturnAuthResponse_WhenValidCredentials() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name("Test User")
                .email("test@test.com")
                .role("CUSTOMER")
                .build();
        
        AuthResponse authResponse = AuthResponse.builder()
                .token("test-token")
                .user(userDTO)
                .build();
        
        when(authService.login(any(AuthRequest.class))).thenReturn(authResponse);
        
        String requestBody = """
                {
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;
        
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}
