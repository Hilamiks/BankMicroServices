package com.hilamiks.accounts.functions;

import com.hilamiks.accounts.service.IAccountsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class AccountsFunctions {

    @Bean
    public Consumer<Long> updateCommunication(IAccountsService accountsService) {
        return accountNumber -> {
            log.info("Updating communication status for account: {}", accountNumber);
            accountsService.updateCommunicationStatus(accountNumber);
        };
    }

}
