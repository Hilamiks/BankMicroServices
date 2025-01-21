package com.hilamiks.accounts.controller;

import com.hilamiks.accounts.aspect.LoggableEndpoint;
import com.hilamiks.accounts.constants.AccountsConstants;
import com.hilamiks.accounts.dto.CustomerDetailsDto;
import com.hilamiks.accounts.service.ICustomersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "CRUD REST API for Customer Details in SomeBank",
    description = "CRUD REST API for Customer Details to CREATE, READ, UPDATE, DELETE accounts information"
)
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private final ICustomersService customerService;

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Operation(
        summary = "Fetch customer details REST API",
        description = "REST API to get info about Customer, Loans, Cards and Account in SomeBank"
    )
    @ApiResponse(
        responseCode = "200",
        description = AccountsConstants.MESSAGE_200
    )
    @GetMapping("/fetchCustomerDetails")
    @LoggableEndpoint
    public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(
        @RequestHeader(name = "somebank-correlation-id", required = false) String correlationId,
        @Pattern(regexp = "[\\d]+") @RequestParam String mobileNumber
    ) {
        log.debug("someBank's correlationId found: {}", correlationId);
        return ResponseEntity.ok(
            customerService.getCustomerDetails(mobileNumber, correlationId));
    }
}
