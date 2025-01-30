package com.hilamiks.accounts.service;

import com.hilamiks.accounts.dto.CustomerDto;

public interface IAccountsService {

    /**
     * @param customerDto - CustomerDto object
     */
    void createAccount(CustomerDto customerDto);

    CustomerDto getCustomer(String mobileNumber);

    boolean updateAccount(CustomerDto customerDto);

    boolean deleteAccount(String mobileNumber);

    boolean updateCommunicationStatus(Long accountNumber);
}
