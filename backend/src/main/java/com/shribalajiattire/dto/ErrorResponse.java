package com.shribalajiattire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private int code;
    private LocalDateTime timestamp;
    
    public static ErrorResponse of(String error, String message, int code) {
        return ErrorResponse.builder()
                .error(error)
                .message(message)
                .code(code)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
