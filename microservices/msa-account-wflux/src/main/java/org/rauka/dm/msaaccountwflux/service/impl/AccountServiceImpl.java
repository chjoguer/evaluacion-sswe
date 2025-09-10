package org.rauka.dm.msaaccountwflux.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rauka.dm.msaaccountwflux.domain.AccountEntity;
import org.rauka.dm.msaaccountwflux.repository.AccountRepository;
import org.rauka.dm.msaaccountwflux.service.AccountService;
import org.rauka.dm.msaaccountwflux.service.models.AccountDTO;
import org.rauka.dm.msaaccountwflux.service.mapper.AccountMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public Mono<AccountDTO> createAccount(AccountDTO accountDTO) {
        log.debug("Creating new account: {}", accountDTO);
        
        AccountEntity entity = accountMapper.toEntity(accountDTO);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        return accountRepository.save(entity)
                .map(accountMapper::toDto)
                .doOnSuccess(savedAccount -> log.debug("Account created successfully: {}", savedAccount))
                .doOnError(error -> log.error("Error creating account: {}", error.getMessage()));
    }

    @Override
    public Mono<AccountDTO> getAccountById(Long accountId) {
        log.debug("Fetching account by ID: {}", accountId);
        
        return accountRepository.findById(accountId)
                .map(accountMapper::toDto)
                .doOnSuccess(account -> log.debug("Account found: {}", account))
                .doOnError(error -> log.error("Error fetching account by ID {}: {}", accountId, error.getMessage()));
    }

    @Override
    public Mono<AccountDTO> getAccountByAccountNumber(String accountNumber) {
        log.debug("Fetching account by account number: {}", accountNumber);
        
        return accountRepository.findByAccountNumber(accountNumber)
                .map(accountMapper::toDto)
                .doOnSuccess(account -> log.debug("Account found by number: {}", account))
                .doOnError(error -> log.error("Error fetching account by number {}: {}", accountNumber, error.getMessage()));
    }

    @Override
    public Flux<AccountDTO> getAllAccounts() {
        log.debug("Fetching all accounts");
        
        return accountRepository.findAll()
                .map(accountMapper::toDto)
                .doOnComplete(() -> log.debug("All accounts fetched successfully"))
                .doOnError(error -> log.error("Error fetching all accounts: {}", error.getMessage()));
    }

    @Override
    public Flux<AccountDTO> getAccountsByType(String accountType) {
        log.debug("Fetching accounts by type: {}", accountType);
        
        AccountEntity.AccountType type = AccountEntity.AccountType.valueOf(accountType.toUpperCase());
        
        return accountRepository.findByAccountType(type)
                .map(accountMapper::toDto)
                .doOnComplete(() -> log.debug("Accounts fetched successfully by type: {}", accountType))
                .doOnError(error -> log.error("Error fetching accounts by type {}: {}", accountType, error.getMessage()));
    }

    @Override
    public Mono<AccountDTO> updateAccount(Long accountId, AccountDTO accountDTO) {
        log.debug("Updating account with ID: {}", accountId);
        
        return accountRepository.findById(accountId)
                .flatMap(existingAccount -> {
                    AccountEntity updatedEntity = accountMapper.toEntity(accountDTO);
                    updatedEntity.setAccountId(accountId);
                    updatedEntity.setCreatedAt(existingAccount.getCreatedAt());
                    updatedEntity.setUpdatedAt(LocalDateTime.now());
                    
                    return accountRepository.save(updatedEntity);
                })
                .map(accountMapper::toDto)
                .doOnSuccess(updatedAccount -> log.debug("Account updated successfully: {}", updatedAccount))
                .doOnError(error -> log.error("Error updating account {}: {}", accountId, error.getMessage()));
    }

    @Override
    public Mono<Void> deleteAccount(Long accountId) {
        log.debug("Deleting account with ID: {}", accountId);
        
        return accountRepository.deleteById(accountId)
                .doOnSuccess(result -> log.debug("Account deleted successfully: {}", accountId))
                .doOnError(error -> log.error("Error deleting account {}: {}", accountId, error.getMessage()));
    }
}
