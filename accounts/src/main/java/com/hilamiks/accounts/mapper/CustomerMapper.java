package com.hilamiks.accounts.mapper;

import com.hilamiks.accounts.dto.CustomerDetailsDto;
import com.hilamiks.accounts.dto.CustomerDto;
import com.hilamiks.accounts.entity.Customer;

public class CustomerMapper {

    public static Customer mapToCustomer(CustomerDto customerDto) {
        return Customer.builder()
            .name(customerDto.getName())
            .email(customerDto.getEmail())
            .mobileNumber(customerDto.getMobileNumber())
            .build();
    }

    public static CustomerDto mapToCustomerDto(Customer customer) {
        return CustomerDto.builder()
            .name(customer.getName())
            .email(customer.getEmail())
            .mobileNumber(customer.getMobileNumber())
            .build();
    }

    public static void merge(CustomerDto customerDto, Customer existingCustomer) {
        existingCustomer.setName(customerDto.getName());
        existingCustomer.setEmail(customerDto.getEmail());
        existingCustomer.setMobileNumber(customerDto.getMobileNumber());
    }

    public static CustomerDetailsDto mapToCustomerDetailsDto(
        Customer customer) {
        return CustomerDetailsDto.builder()
            .name(customer.getName())
            .email(customer.getEmail())
            .mobileNumber(customer.getMobileNumber())
            .build();
    }
}
