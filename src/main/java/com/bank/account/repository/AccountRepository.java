package com.bank.account.repository;

import com.bank.account.domain.Account;
import com.bank.account.domain.AccountType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

    Mono<Boolean> existsByAccountNumber(String accountNumber);
    Flux<Account> findByCustomerId(String customerId);
    Mono<Boolean> existsByCustomerIdAndAccountTypeAndActiveTrue(
            String customerId,
            AccountType accountType
    );
    Flux<Account> findByCustomerIdAndAccountTypeAndActiveTrue(
            String customerId,
            AccountType accountType
    );
    Flux<Account> findByCustomerIdAndActiveTrue(String customerId);
    Mono<Account> findByIdAndActiveTrue(String id);
}