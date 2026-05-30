package com.bank.account.mapper;

import com.bank.account.api.dto.AccountRequest;
import com.bank.account.api.dto.AccountResponse;
import com.bank.account.domain.Account;
import com.bank.account.domain.AccountType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class AccountMapper {

    /**
     * Maps request DTO to domain entity.
     *
     * @param request account request
     * @return account entity
     */
    public Account toEntity(AccountRequest request) {

        return Account.builder()
                .customerId(request.getCustomerId())
                .accountType(AccountType.valueOf(request.getAccountType()))
                .maintenanceFee(
                        request.getMaintenanceFee() == null
                                ? BigDecimal.ZERO
                                : BigDecimal.valueOf(request.getMaintenanceFee())
                )
                .monthlyMovementLimit(request.getMonthlyMovementLimit())
                .allowedMovementDay(request.getAllowedMovementDay())
                .holders(request.getHolders())
                .authorizedSigners(request.getAuthorizedSigners())
                .balance(BigDecimal.ZERO)
                .active(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Maps domain entity to response DTO.
     *
     * @param account account entity
     * @return account response
     */
    public AccountResponse toResponse(Account account) {

        AccountResponse response = new AccountResponse();

        response.setId(account.getId());
        response.setCustomerId(account.getCustomerId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType().name());

        response.setBalance(
                account.getBalance() == null
                        ? 0.0
                        : account.getBalance().doubleValue()
        );

        response.setMaintenanceFee(
                account.getMaintenanceFee() == null
                        ? 0.0
                        : account.getMaintenanceFee().doubleValue()
        );

        response.setMonthlyMovementLimit(account.getMonthlyMovementLimit());
        response.setAllowedMovementDay(account.getAllowedMovementDay());
        response.setHolders(account.getHolders());
        response.setAuthorizedSigners(account.getAuthorizedSigners());
        response.setActive(account.getActive());

        return response;
    }

    /**
     * Updates an existing account entity.
     *
     * @param account existing account
     * @param request request data
     * @return updated account
     */
    public Account updateEntity(
            Account account,
            AccountRequest request) {

        account.setCustomerId(request.getCustomerId());
        account.setAccountType(AccountType.valueOf(request.getAccountType()));

        account.setMaintenanceFee(
                request.getMaintenanceFee() == null
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(request.getMaintenanceFee())
        );

        account.setMonthlyMovementLimit(request.getMonthlyMovementLimit());
        account.setAllowedMovementDay(request.getAllowedMovementDay());
        account.setHolders(request.getHolders());
        account.setAuthorizedSigners(request.getAuthorizedSigners());

        account.setUpdatedAt(LocalDateTime.now());

        return account;
    }
}