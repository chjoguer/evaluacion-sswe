package org.rauka.dm.msaaccountwflux.service;

import org.rauka.dm.msaaccountwflux.service.models.AccountDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {
    
    Mono<AccountDTO> createAccount(AccountDTO accountDTO);
    
    Mono<AccountDTO> getAccountById(Long accountId);
    
    Mono<AccountDTO> getAccountByAccountNumber(String accountNumber);
    
    Flux<AccountDTO> getAllAccounts();
    
    Flux<AccountDTO> getAccountsByType(String accountType);
    
    Mono<AccountDTO> updateAccount(Long accountId, AccountDTO accountDTO);
    
    Mono<Void> deleteAccount(Long accountId);
}
