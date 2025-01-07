package com.hilamiks.accounts.mapper;

import com.hilamiks.accounts.dto.AccountsDto;
import com.hilamiks.accounts.entity.Accounts;

public class AccountsMapper {
    public static AccountsDto mapToAccountsDto(Accounts accounts) {
        return AccountsDto.builder()
            .accountNumber(accounts.getAccountNumber())
            .accountType(accounts.getAccountType())
            .branchAddress(accounts.getBranchAddress())
            .build();
    }

    public static Accounts mapToAccounts(AccountsDto dto) {
        return Accounts.builder()
            .accountNumber(dto.getAccountNumber())
            .accountType(dto.getAccountType())
            .branchAddress(dto.getBranchAddress())
            .build();
    }

    public static void merge(AccountsDto incoming, Accounts existing) {
        existing.setAccountNumber(incoming.getAccountNumber());
        existing.setAccountType(incoming.getAccountType());
        existing.setBranchAddress(incoming.getBranchAddress());
    }
}
