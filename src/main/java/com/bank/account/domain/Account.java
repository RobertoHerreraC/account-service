package com.bank.account.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class Account {

    @Id
    private String id;

    private String customerId;

    private String accountNumber;

    private AccountType accountType;

    private BigDecimal balance;

    private BigDecimal maintenanceFee;

    private Integer monthlyMovementLimit;

    private Integer allowedMovementDay;

    private List<String> holders;

    private List<String> authorizedSigners;

    private Boolean active;
}