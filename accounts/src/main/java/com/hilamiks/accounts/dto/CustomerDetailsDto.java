package com.hilamiks.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(
    name = "CustomerDetails",
    description = "Schema to hold Customer, Loans, Cards and Account information"
)
public class CustomerDetailsDto {
    @NotEmpty(message = "Name cannot be empty")
    @Size(min=1, max=50, message = "The name is too long!")
    @Schema(
        description = "Full name of the account owner",
        example = "John Doe"
    )
    private String name;
    @Email(message = "Email does not fit the format")
    @NotEmpty
    @Schema(
        description = "Email of the account owner",
        example = "johnDoe@mail.com"
    )
    private String email;
    @NotEmpty
    @Pattern(regexp = "[\\d]+")
    @Size(min=8, max=20)
    @Schema(
        description = "Contact number of the account owner",
        example = "12345678900"
    )
    private String mobileNumber;
    @Schema(
        description = "Accounts associated with the customer"
    )
    private AccountsDto accounts;
    @Schema(
        description = "Cards associated with the customer"
    )
    private CardsDto cards;
    @Schema(
        description = "Loans associated with the customer"
    )
    private LoansDto loans;
}
