package com.hilamiks.accounts.service.impl;

import com.hilamiks.accounts.dto.CardsDto;
import com.hilamiks.accounts.dto.CustomerDetailsDto;
import com.hilamiks.accounts.dto.LoansDto;
import com.hilamiks.accounts.entity.Accounts;
import com.hilamiks.accounts.entity.Customer;
import com.hilamiks.accounts.exception.ResourceNotFoundException;
import com.hilamiks.accounts.mapper.AccountsMapper;
import com.hilamiks.accounts.mapper.CustomerMapper;
import com.hilamiks.accounts.repository.AccountsRepository;
import com.hilamiks.accounts.repository.CustomerRepository;
import com.hilamiks.accounts.service.ICustomersService;
import com.hilamiks.accounts.service.client.CardsFeignClient;
import com.hilamiks.accounts.service.client.LoansFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private final AccountsRepository accountsRepository;
    private final CustomerRepository customerRepository;
    private final CardsFeignClient cardsFeignClient;
    private final LoansFeignClient loansFeignClient;

    @Override
    public CustomerDetailsDto getCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer =
            customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException(
                    String.format("Customer with this mobile number %s does not exist",
                        mobileNumber))
            );
        Accounts accounts =
            accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("Account for customer %s does not exist",
                        mobileNumber)));

        CustomerDetailsDto result = CustomerMapper.mapToCustomerDetailsDto(
            customer
        );
        result.setAccounts(AccountsMapper.mapToAccountsDto(accounts));

        System.out.println("one");
        ResponseEntity<LoansDto> loansResponse =
            loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        result.setLoans(loansResponse.getBody());

        System.out.println("two");
        ResponseEntity<CardsDto> cardsResponse =
            cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        result.setCards(cardsResponse.getBody());

        System.out.println("three");
        System.out.println(result);
        return result;
    }
}
