package com.hilamiks.accounts.service.impl;

import com.hilamiks.accounts.dto.AccountsDto;
import com.hilamiks.accounts.dto.AccountsMsgDto;
import com.hilamiks.accounts.dto.CustomerDto;
import com.hilamiks.accounts.entity.Accounts;
import com.hilamiks.accounts.entity.Customer;
import com.hilamiks.accounts.exception.CustomerAlreadyExistsException;
import com.hilamiks.accounts.exception.ResourceNotFoundException;
import com.hilamiks.accounts.mapper.AccountsMapper;
import com.hilamiks.accounts.mapper.CustomerMapper;
import com.hilamiks.accounts.repository.AccountsRepository;
import com.hilamiks.accounts.repository.CustomerRepository;
import com.hilamiks.accounts.service.IAccountsService;
import com.hilamiks.accounts.constants.AccountsConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements IAccountsService {

    private final AccountsRepository accountsRepository;
    private final CustomerRepository customerRepository;
    private final StreamBridge streamBridge;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createAccount(CustomerDto customerDto) {
        if (customerRepository.findByMobileNumber(
            customerDto.getMobileNumber()).isPresent()) {
            throw new CustomerAlreadyExistsException("Customer with this mobile number already exists");
        }
        Customer toSave = CustomerMapper.mapToCustomer(customerDto);
        Customer saved = customerRepository.save(
            toSave);
        Accounts account = createAccountForNewCustomer(saved);
        sendCommunication(accountsRepository.save(account), saved);
    }

    private void sendCommunication(Accounts account, Customer customer) {
        AccountsMsgDto dto = new AccountsMsgDto(
            account.getAccountNumber(),
            customer.getName(),
            customer.getEmail(),
            customer.getMobileNumber()
        );

        log.info("Sending communication request for the details: {}", dto);
        boolean result = streamBridge.send("sendCommunication-out-0", dto);
        log.info("Request processed?: {}", result);
    }

    @Override
    public CustomerDto getCustomer(String mobileNumber) {
        Customer customer =
            customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Customer with this mobile number %s does not exist", mobileNumber))
            );
        Accounts accounts =
            accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Account for customer %s does not exist", mobileNumber)));
        CustomerDto response = CustomerMapper.mapToCustomerDto(customer);
        response.setAccounts(AccountsMapper.mapToAccountsDto(accounts));
        return response;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountsDto incoming = customerDto.getAccounts();
        if (incoming != null) {
            Accounts existingAccount = accountsRepository.findById(incoming.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format(
                        "Account number %s is not associated with any account",
                        incoming.getAccountNumber())));

            AccountsMapper.merge(incoming, existingAccount);
            accountsRepository.save(existingAccount);

            Customer existingCustomer = customerRepository.findById(
                existingAccount.getCustomerId()
            ).orElseThrow(() -> new ResourceNotFoundException(
                String.format("No customer is associated with id of: %s", existingAccount.getCustomerId()
                )));
            CustomerMapper.merge(customerDto, existingCustomer);
            customerRepository.save(existingCustomer);
            isUpdated = true;
        }
        return isUpdated;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
            .orElseThrow(
                () -> new ResourceNotFoundException(String.format("Customer with this mobile number %s does not exist", mobileNumber))
            );
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

    private Accounts createAccountForNewCustomer(Customer saved) {
        Accounts account = new Accounts();
        account.setCustomerId(saved.getCustomerId());
        account.setAccountNumber(1000000000L + new Random().nextLong(9000000000L));
        account.setAccountType(AccountsConstants.SAVINGS);
        account.setBranchAddress(AccountsConstants.ADDRESS);
        return account;
    }

    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdated = false;

        if (accountNumber != null) {
            Accounts accounts = accountsRepository.findById(accountNumber).orElseThrow(
                () -> new ResourceNotFoundException("Account number " + accountNumber + " does not exist")
            );
            accounts.setCommunicationSw(true);
            accountsRepository.save(accounts);
            isUpdated = true;
        }

        return isUpdated;
    }

}
