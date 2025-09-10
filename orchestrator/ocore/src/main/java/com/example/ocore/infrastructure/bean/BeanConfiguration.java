package com.example.ocore.infrastructure.bean;

import com.example.ocore.application.input.port.AccountUseCase;
import com.example.ocore.application.input.port.ClientUseCase;
import com.example.ocore.application.input.port.MovementUseCase;
import com.example.ocore.application.output.port.AccountServicePort;
import com.example.ocore.application.output.port.ClientServicePort;
import com.example.ocore.application.output.port.MovementServicePort;
import com.example.ocore.application.service.AccountService;
import com.example.ocore.application.service.ClientService;
import com.example.ocore.application.service.MovementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public MovementUseCase movementUseCase(MovementServicePort movementServicePort) {
        return new MovementService(movementServicePort);
    }

    @Bean
    public AccountUseCase accountUseCase(AccountServicePort accountServicePort) {
        return new AccountService(accountServicePort);
    }

    @Bean
    public ClientUseCase clientUseCase(ClientServicePort clientServicePort) {
        return new ClientService(clientServicePort);
    }
}
