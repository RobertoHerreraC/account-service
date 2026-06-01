package com.bank.account.client.movement;

import com.bank.account.client.movement.dto.MovementRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MovementClient {

    private final WebClient webClient;

    @Value("${movement-service.base-url}")
    private String movementServiceBaseUrl;

    public Mono<Void> createMovement(MovementRequest request) {
        return webClient
                .post()
                .uri(movementServiceBaseUrl + "/api/v1/movements")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class);
    }
}