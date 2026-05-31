package com.bank.account.service;

import com.bank.account.api.dto.*;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface AccountService {
    Single<AccountResponse> create(AccountRequest request);
    Flowable<AccountResponse> findAll();
    Single<AccountResponse> findById(String id);
    Single<AccountResponse> update(String id, AccountRequest request);
    Completable delete(String id);
    Single<AccountResponse> deposit(String id, AccountDepositRequest request);
    Single<AccountResponse> withdraw(String id, AccountWithdrawalRequest request);
    Single<AccountBalanceResponse> getBalance(String id);
    Flowable<AccountResponse> findByCustomerId(String customerId);
}