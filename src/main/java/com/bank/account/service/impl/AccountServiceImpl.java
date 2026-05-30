package com.bank.account.service.impl;

import com.bank.account.api.dto.AccountRequest;
import com.bank.account.api.dto.AccountResponse;
import com.bank.account.client.CustomerClient;
import com.bank.account.client.dto.CustomerResponse;
import com.bank.account.domain.Account;
import com.bank.account.domain.AccountType;
import com.bank.account.exception.AccountNotFoundException;
import com.bank.account.exception.BusinessRuleException;
import com.bank.account.exception.CustomerNotFoundException;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.repository.AccountRepository;
import com.bank.account.service.AccountService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final String PERSONAL = "PERSONAL";
    private static final String BUSINESS = "BUSINESS";

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CustomerClient customerClient;

    @Override
    public Single<AccountResponse> create(AccountRequest request) {
        log.info("Creating account for customerId: {}", request.getCustomerId());

        AccountType accountType = parseAccountType(request.getAccountType());

        return findCustomerOrFail(request.getCustomerId())
                .flatMap(customer -> validateCreateRules(customer, accountType, request))
                .map(validatedRequest -> accountMapper.toEntity(validatedRequest))
                .flatMap(account ->
                        Single.fromPublisher(accountRepository.save(account))
                )
                .map(accountMapper::toResponse)
                .doOnSuccess(response ->
                        log.info("Account created successfully with id: {}", response.getId()));
    }

    @Override
    public Flowable<AccountResponse> findAll() {
        log.info("Finding all accounts");

        return Flowable.fromPublisher(accountRepository.findAll())
                .map(accountMapper::toResponse);
    }

    @Override
    public Single<AccountResponse> findById(String id) {
        log.info("Finding account by id: {}", id);

        return Single.fromPublisher(
                        accountRepository.findById(id)
                                .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
                )
                .map(accountMapper::toResponse);
    }

    @Override
    public Single<AccountResponse> update(String id, AccountRequest request) {
        log.info("Updating account with id: {}", id);

        AccountType accountType = parseAccountType(request.getAccountType());

        return Single.fromPublisher(
                        accountRepository.findById(id)
                                .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
                )
                .flatMap(existingAccount ->
                        findCustomerOrFail(request.getCustomerId())
                                .flatMap(customer -> validateUpdateRules(customer, accountType, existingAccount, request))
                                .map(validatedRequest -> accountMapper.updateEntity(existingAccount, validatedRequest))
                )
                .flatMap(updatedAccount ->
                        Single.fromPublisher(accountRepository.save(updatedAccount))
                )
                .map(accountMapper::toResponse)
                .doOnSuccess(response ->
                        log.info("Account updated successfully with id: {}", response.getId()));
    }

    @Override
    public Completable delete(String id) {
        log.info("Deleting account logically with id: {}", id);

        return Single.fromPublisher(
                        accountRepository.findById(id)
                                .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
                )
                .map(account -> {
                    account.setActive(Boolean.FALSE);
                    return account;
                })
                .flatMap(account ->
                        Single.fromPublisher(accountRepository.save(account))
                )
                .ignoreElement();
    }

    private Single<CustomerResponse> findCustomerOrFail(String customerId) {
        return Single.fromPublisher(
                customerClient.findCustomerById(customerId)
                        .switchIfEmpty(Mono.error(new CustomerNotFoundException(customerId)))
                        .onErrorMap(error -> new CustomerNotFoundException(customerId))
        );
    }

    private Single<AccountRequest> validateCreateRules(
            CustomerResponse customer,
            AccountType accountType,
            AccountRequest request) {

        validateCustomerAccountType(customer, accountType, request);

        if (PERSONAL.equals(customer.getCustomerType())
                && (AccountType.SAVINGS.equals(accountType)
                || AccountType.CHECKING.equals(accountType))) {

            return validatePersonalUniqueAccount(customer.getId(), accountType)
                    .andThen(Single.just(request));
        }

        return Single.just(request);
    }

    private Single<AccountRequest> validateUpdateRules(
            CustomerResponse customer,
            AccountType accountType,
            Account existingAccount,
            AccountRequest request) {

        validateCustomerAccountType(customer, accountType, request);

        return Single.just(request);
    }

    private Completable validatePersonalUniqueAccount(
            String customerId,
            AccountType accountType) {
        return Single.fromPublisher(
                        accountRepository.existsByCustomerIdAndAccountTypeAndActiveTrue(
                                customerId,
                                accountType
                        )
                )
                .flatMapCompletable(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Completable.error(
                                new BusinessRuleException(
                                        "Personal customer already has an active "
                                                + accountType.name()
                                                + " account"
                                )
                        );
                    }
                    return Completable.complete();
                });
    }

    private void validateCustomerAccountType(
            CustomerResponse customer,
            AccountType accountType,
            AccountRequest request) {

        if (BUSINESS.equals(customer.getCustomerType())) {
            validateBusinessCustomer(accountType, request);
        }

        if (PERSONAL.equals(customer.getCustomerType())) {
            validatePersonalCustomer(accountType);
        }
    }

    private void validateBusinessCustomer(
            AccountType accountType,
            AccountRequest request) {

        if (AccountType.SAVINGS.equals(accountType)
                || AccountType.FIXED_TERM.equals(accountType)) {

            throw new BusinessRuleException(
                    "Business customers can only have checking accounts"
            );
        }

        if (request.getHolders() == null || request.getHolders().isEmpty()) {
            throw new BusinessRuleException(
                    "Business accounts must have at least one holder"
            );
        }
    }

    private void validatePersonalCustomer(AccountType accountType) {
        if (accountType == null) {
            throw new BusinessRuleException("Account type is required");
        }
    }

    private AccountType parseAccountType(String accountType) {
        try {
            return AccountType.valueOf(accountType);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new BusinessRuleException("Invalid account type: " + accountType);
        }
    }
}
