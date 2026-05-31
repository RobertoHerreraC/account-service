package com.bank.account.controller;

import com.bank.account.api.dto.*;
import com.bank.account.api.generated.AccountsApi;
import com.bank.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountsApi {

    private final AccountService accountService;

    /**
     * Creates a new bank account.
     *
     * @param accountRequest account request body
     * @param exchange current server exchange
     * @return created account response
     */
    @Override
    public Mono<ResponseEntity<AccountResponse>> createAccount(
            @Valid Mono<AccountRequest> accountRequest,
            ServerWebExchange exchange) {

        return accountRequest
                .flatMap(request ->
                        Mono.fromCompletionStage(
                                accountService.create(request).toCompletionStage()
                        )
                )
                .map(response ->
                        ResponseEntity.status(HttpStatus.CREATED).body(response)
                );
    }

    /**
     * Finds all bank accounts.
     *
     * @param exchange current server exchange
     * @return account list response
     */
    @Override
    public Mono<ResponseEntity<Flux<AccountResponse>>> findAllAccounts(
            ServerWebExchange exchange) {

        Flux<AccountResponse> accounts =
                Flux.from(accountService.findAll());

        return Mono.just(ResponseEntity.ok(accounts));
    }

    /**
     * Finds a bank account by id.
     *
     * @param id account unique identifier
     * @param exchange current server exchange
     * @return account response
     */
    @Override
    public Mono<ResponseEntity<AccountResponse>> findAccountById(
            String id,
            ServerWebExchange exchange) {

        return Mono.fromCompletionStage(
                        accountService.findById(id).toCompletionStage()
                )
                .map(ResponseEntity::ok);
    }

    /**
     * Updates a bank account by id.
     *
     * @param id account unique identifier
     * @param accountRequest account request body
     * @param exchange current server exchange
     * @return updated account response
     */
    @Override
    public Mono<ResponseEntity<AccountResponse>> updateAccount(
            String id,
            @Valid Mono<AccountRequest> accountRequest,
            ServerWebExchange exchange) {

        return accountRequest
                .flatMap(request ->
                        Mono.fromCompletionStage(
                                accountService.update(id, request).toCompletionStage()
                        )
                )
                .map(ResponseEntity::ok);
    }

    /**
     * Deletes a bank account by id.
     *
     * @param id account unique identifier
     * @param exchange current server exchange
     * @return empty response
     */
    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(
            String id,
            ServerWebExchange exchange) {

        return Mono.fromCompletionStage(
                        accountService.delete(id).toCompletionStage(null)
                )
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> depositAccount(
            String id,
            Mono<AccountDepositRequest> accountDepositRequest,
            ServerWebExchange exchange) {

        return accountDepositRequest
                .flatMap(request ->
                        Mono.fromCompletionStage(
                                accountService.deposit(id, request).toCompletionStage()
                        )
                )
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> withdrawAccount(
            String id,
            Mono<AccountWithdrawalRequest> accountWithdrawalRequest,
            ServerWebExchange exchange) {

        return accountWithdrawalRequest
                .flatMap(request ->
                        Mono.fromCompletionStage(
                                accountService.withdraw(id, request).toCompletionStage()
                        )
                )
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AccountBalanceResponse>> getAccountBalance(
            String id,
            ServerWebExchange exchange) {

        return Mono.fromCompletionStage(
                        accountService.getBalance(id).toCompletionStage()
                )
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<AccountResponse>>> findAccountsByCustomerId(
            String customerId,
            ServerWebExchange exchange) {
        return Mono.just(
                ResponseEntity.ok(
                        Flux.from(accountService.findByCustomerId(customerId))
                )
        );
    }
}