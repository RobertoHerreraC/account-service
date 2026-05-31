package com.bank.account.mapper;

import com.bank.account.api.dto.AccountBalanceResponse;
import com.bank.account.api.dto.AccountRequest;
import com.bank.account.api.dto.AccountResponse;
import com.bank.account.domain.Account;
import com.bank.account.domain.AccountType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Component
public class AccountMapper {

    public Account toEntity(AccountRequest request) {
        return Account.builder()
                .customerId(request.getCustomerId())
                .accountNumber(generateAccountNumber())
                .accountType(AccountType.valueOf(request.getAccountType()))
                .balance(BigDecimal.ZERO)
                .maintenanceFee(toBigDecimal(request.getMaintenanceFee()))
                .monthlyMovementLimit(request.getMonthlyMovementLimit())
                .allowedMovementDay(request.getAllowedMovementDay())
                .holders(defaultList(request.getHolders()))
                .authorizedSigners(defaultList(request.getAuthorizedSigners()))
                .active(Boolean.TRUE)
                .build();
    }

    public AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setCustomerId(account.getCustomerId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType().name());
        response.setBalance(account.getBalance().doubleValue());
        response.setMaintenanceFee(account.getMaintenanceFee().doubleValue());
        response.setMonthlyMovementLimit(account.getMonthlyMovementLimit());
        response.setAllowedMovementDay(account.getAllowedMovementDay());
        response.setHolders(account.getHolders());
        response.setAuthorizedSigners(account.getAuthorizedSigners());
        response.setActive(account.getActive());
        return response;
    }

    public Account updateEntity(Account account, AccountRequest request) {
        account.setCustomerId(request.getCustomerId());
        account.setAccountType(AccountType.valueOf(request.getAccountType()));
        account.setMaintenanceFee(toBigDecimal(request.getMaintenanceFee()));
        account.setMonthlyMovementLimit(request.getMonthlyMovementLimit());
        account.setAllowedMovementDay(request.getAllowedMovementDay());
        account.setHolders(defaultList(request.getHolders()));
        account.setAuthorizedSigners(defaultList(request.getAuthorizedSigners()));
        return account;
    }

    public AccountBalanceResponse toBalanceResponse(Account account) {
        AccountBalanceResponse response = new AccountBalanceResponse();

        response.setAccountId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType().name());
        response.setBalance(account.getBalance().doubleValue());
        return response;
    }

    private BigDecimal toBigDecimal(Double value) {
        return value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value);
    }

    private java.util.List<String> defaultList(java.util.List<String> values) {
        return values == null ? Collections.emptyList() : values;
    }

    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}