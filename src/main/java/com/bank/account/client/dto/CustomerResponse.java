package com.bank.account.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerResponse {

    private String id;

    private String documentNumber;

    private String fullName;

    private String customerType;

    private String email;

    private String phone;

    private String address;

    private Boolean active;
}