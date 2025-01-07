package com.hilamiks.accounts.dto;

import com.hilamiks.accounts.constants.AccountsConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(
    name = "Response",
    description = "Schema to hold successful response information"
)
public class ResponseDto {
    @Schema(
        description = "Status code in the response",
        example = "200"
    )
    private String status;
    @Schema(
        description = "Status message in the response",
        example = AccountsConstants.MESSAGE_200
    )
    private String message;
}
