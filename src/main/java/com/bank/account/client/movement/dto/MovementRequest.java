package com.bank.account.client.movement.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovementRequest {
    private String customerId;
    private String productId;
    private String productType;
    private String movementType;
    private Double amount;
    private String description;
}
