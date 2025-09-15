package org.rauka.dm.msapdfgenerator.service;

import org.rauka.dm.msapdfgenerator.dto.AccountDTO;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<AccountDTO> getAccountById(Long accountId);

}
