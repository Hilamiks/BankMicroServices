package com.hilamiks.accounts.dto;

import com.hilamiks.accounts.constants.AccountsConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(
    name = "Account",
    description = "Schema to hold Account information"
)
public class AccountsDto {
    @NotEmpty(message = "Account number is mandatory")
    @Pattern(regexp = "[\\d]{10}")
    @Schema(
        description = "Account number of SomeBank account"
    )
    private Long accountNumber;
    @NotEmpty(message = "Account type is mandatory")
    @Schema(
        description = "Account type of SomeBank account",
        example = AccountsConstants.SAVINGS
    )
    private String accountType;
    @NotEmpty(message = "Branch address is mandatory")
    @Schema(
        description = "SomeBank branch address"
    )
    private String branchAddress;
}
