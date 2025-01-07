package com.hilamiks.accounts.service;

import com.hilamiks.accounts.dto.CustomerDetailsDto;

public interface ICustomersService {
    CustomerDetailsDto getCustomerDetails(String mobileNumber, String correlationId);
}
