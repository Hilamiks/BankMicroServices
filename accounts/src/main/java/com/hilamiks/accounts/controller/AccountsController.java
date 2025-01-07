package com.hilamiks.accounts.controller;

import com.hilamiks.accounts.dto.AccountsContactInfoDto;
import com.hilamiks.accounts.dto.CustomerDto;
import com.hilamiks.accounts.dto.ErrorResponseDto;
import com.hilamiks.accounts.dto.ResponseDto;
import com.hilamiks.accounts.service.IAccountsService;
import com.hilamiks.accounts.constants.AccountsConstants;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "CRUD REST API for Accounts in SomeBank",
    description = "CRUD REST API for Accounts to CREATE, READ, UPDATE, DELETE accounts information"
)
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class AccountsController {

    private final IAccountsService accountsService;

    @Value("${build.version}")
    private String buildVersion;

    private final Environment environment;

    private final AccountsContactInfoDto contactInfoDto;

    @PostMapping("/create")
    @Operation(
        summary = "Create account REST API",
        description = "REST API to create a new Customer and Account in SomeBank"
    )
    @ApiResponse(
        responseCode = "201",
        description = AccountsConstants.MESSAGE_201
    )
    public ResponseEntity<ResponseDto> createCustomer(
        @Valid @RequestBody final CustomerDto customerDto
    ) {
        accountsService.createAccount(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDto.builder()
                .message(AccountsConstants.MESSAGE_201)
                .status(AccountsConstants.STATUS_201)
                .build());
    }

    @Operation(
        summary = "Fetch account REST API",
        description = "REST API to get info about Customer and Account in SomeBank"
    )
    @ApiResponse(
        responseCode = "200",
        description = AccountsConstants.MESSAGE_200
    )
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> getCustomer(
        @Pattern(regexp = "[\\d]+") @RequestParam String mobileNumber
    ) {
        return ResponseEntity.ok()
            .body(accountsService.getCustomer(mobileNumber));
    }

    @Operation(
        summary = "Update account REST API",
        description = "REST API to get info about Customer and Account in SomeBank"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = AccountsConstants.MESSAGE_200
            ),
            @ApiResponse(
                responseCode = "500",
                description = AccountsConstants.MESSAGE_500,
                content = @Content(
                    schema = @Schema(
                        implementation = ErrorResponseDto.class
                    )
                )
            )
        }
    )
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateCustomer(
        @Valid @RequestBody final CustomerDto customerDto
    ) {
        boolean isUpdated = accountsService.updateAccount(customerDto);
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.builder()
                    .message(AccountsConstants.MESSAGE_200)
                    .status(AccountsConstants.STATUS_200)
                    .build());
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
            .body(ResponseDto.builder()
                .message(AccountsConstants.MESSAGE_500)
                .status(AccountsConstants.STATUS_500)
                .build());
    }

    @Operation(
        summary = "Delete account REST API",
        description = "REST API to delete info about Customer and Account in SomeBank"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = AccountsConstants.MESSAGE_200
            ),
            @ApiResponse(
                responseCode = "500",
                description = AccountsConstants.MESSAGE_500,
                content = @Content(
                    schema = @Schema(
                        implementation = ErrorResponseDto.class
                    )
                )
            )
        }
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCustomer(
        @Pattern(regexp = "[\\d]+") @RequestParam String mobileNumber
    ) {
        boolean isDeleted = accountsService.deleteAccount(mobileNumber);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseDto.builder()
                    .message(AccountsConstants.MESSAGE_200)
                    .status(AccountsConstants.STATUS_200)
                    .build());
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
            .body(ResponseDto.builder()
                .message(AccountsConstants.MESSAGE_500)
                .status(AccountsConstants.STATUS_500)
                .build());
    }

    @Operation(
        summary = "Get build information",
        description = "REST API to get build information about the Accounts microservice"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = AccountsConstants.MESSAGE_200
            ),
            @ApiResponse(
                responseCode = "500",
                description = AccountsConstants.MESSAGE_500,
                content = @Content(
                    schema = @Schema(
                        implementation = ErrorResponseDto.class
                    )
                )
            )
        }
    )
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo() {
        return ResponseEntity.ok(buildVersion);
    }

    @Operation(
        summary = "Get java version",
        description = "REST API to get java version of Accounts microservice"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = AccountsConstants.MESSAGE_200
            ),
            @ApiResponse(
                responseCode = "500",
                description = AccountsConstants.MESSAGE_500,
                content = @Content(
                    schema = @Schema(
                        implementation = ErrorResponseDto.class
                    )
                )
            )
        }
    )
    @GetMapping("/java-version")
    @Retry(name = "getJavaVersion", fallbackMethod = "getJavaVersionFallback")
    public ResponseEntity<String> getJavaVersion() {
        System.out.println("invoked getJavaVersion");
        //throw new NullPointerException("hi");
        return ResponseEntity.ok(environment.getProperty("JAVA_HOME"));
    }

    public ResponseEntity<String> getJavaVersionFallback(Throwable t) {
        System.out.println(t.getMessage());
        System.out.println("invoked getJavaVersionFallback");
        return ResponseEntity.ok("idk, but probably above 8, may be 17 if u lucky");
    }

    @Operation(
        summary = "Get contact details",
        description = "REST API to get contact info for Accounts microservice"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = AccountsConstants.MESSAGE_200
            ),
            @ApiResponse(
                responseCode = "500",
                description = AccountsConstants.MESSAGE_500,
                content = @Content(
                    schema = @Schema(
                        implementation = ErrorResponseDto.class
                    )
                )
            )
        }
    )
    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactInfoDto> getContactInfo() {
        return ResponseEntity.ok(contactInfoDto);
    }
}
