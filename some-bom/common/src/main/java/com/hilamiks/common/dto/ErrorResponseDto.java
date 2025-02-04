package com.hilamiks.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(
    name = "ErrorResponse",
    description = "Schema to hold error response information"
)
public class ErrorResponseDto {
    @Schema(
        description = "API path that was invoked"
    )
    private String apiPath;
    @Schema(
        description = "HTTP status of the response"
    )
    private HttpStatus errorCode;
    @Schema(
        description = "Explanation of the error"
    )
    private String errorMessage;
    @Schema(
        description = "When the error occurred"
    )
    private LocalDateTime timestamp;
}