package com.bank.account.repository;

import com.bank.account.domain.Account;
import com.bank.account.domain.AccountType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

    /**
     * Finds all accounts associated with a customer.
     *
     * @param customerId customer unique identifier
     * @return customer accounts
     */
    Flux<Account> findByCustomerId(String customerId);

    /**
     * Finds all accounts associated with a customer and account type.
     *
     * @param customerId customer unique identifier
     * @param accountType account type
     * @return customer accounts by type
     */
    Flux<Account> findByCustomerIdAndAccountType(String customerId, AccountType accountType);

    /**
     * Checks whether an account number already exists.
     *
     * @param accountNumber account number
     * @return true if account number exists
     */
    Mono<Boolean> existsByAccountNumber(String accountNumber);

    /**
     * Counts accounts by customer and account type.
     *
     * @param customerId customer unique identifier
     * @param accountType account type
     * @return number of accounts
     */
    Mono<Long> countByCustomerIdAndAccountType(String customerId, AccountType accountType);
}