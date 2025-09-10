package org.rauka.dm.msaaccountwflux.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rauka.dm.msaaccountwflux.domain.AccountEntity;
import org.rauka.dm.msaaccountwflux.repository.AccountRepository;
import org.rauka.dm.msaaccountwflux.service.mapper.AccountMapper;
import org.rauka.dm.msaaccountwflux.service.models.AccountDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private AccountEntity testEntity;
    private AccountDTO testDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        testEntity = AccountEntity.builder()
                .accountId(1L)
                .accountNumber("ACC-001-SAVINGS")
                .identification("12345678")
                .accountType(AccountEntity.AccountType.SAVINGS)
                .balance(new BigDecimal("1500.50"))
                .createdAt(now)
                .updatedAt(now)
                .build();

        testDTO = new AccountDTO()
                .accountId(1)
                .accountNumber("ACC-001-SAVINGS")
                .identification("12345678")
                .accountType(AccountDTO.AccountTypeEnum.SAVINGS)
                .balance(new BigDecimal("1500.50"))
                .createdAt(OffsetDateTime.of(now, ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.of(now, ZoneOffset.UTC));
    }

    @Test
    void createAccount_ShouldCreateAccountWithIdentification_WhenValidDataProvided() {
        when(accountMapper.toEntity(any(AccountDTO.class))).thenReturn(testEntity);
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(Mono.just(testEntity));
        when(accountMapper.toDto(any(AccountEntity.class))).thenReturn(testDTO);

        StepVerifier.create(accountService.createAccount(testDTO))
                .assertNext(savedAccount -> {
                    assertThat(savedAccount).isNotNull();
                    assertThat(savedAccount.getAccountId()).isEqualTo(1);
                    assertThat(savedAccount.getAccountNumber()).isEqualTo("ACC-001-SAVINGS");
                    assertThat(savedAccount.getIdentification()).isEqualTo("12345678");
                    assertThat(savedAccount.getAccountType()).isEqualTo(AccountDTO.AccountTypeEnum.SAVINGS);
                    assertThat(savedAccount.getBalance()).isEqualTo(new BigDecimal("1500.50"));
                })
                .verifyComplete();
    }

    @Test
    void getAccountById_ShouldReturnAccountWithIdentification_WhenAccountExists() {
        when(accountRepository.findById(anyLong())).thenReturn(Mono.just(testEntity));
        when(accountMapper.toDto(any(AccountEntity.class))).thenReturn(testDTO);

        StepVerifier.create(accountService.getAccountById(1L))
                .assertNext(account -> {
                    assertThat(account).isNotNull();
                    assertThat(account.getAccountId()).isEqualTo(1);
                    assertThat(account.getIdentification()).isEqualTo("12345678");
                    assertThat(account.getAccountNumber()).isEqualTo("ACC-001-SAVINGS");
                })
                .verifyComplete();
    }

    @Test
    void getAccountById_ShouldReturnEmpty_WhenAccountDoesNotExist() {
        when(accountRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(accountService.getAccountById(999L))
                .verifyComplete();
    }

    @Test
    void getAccountByAccountNumber_ShouldReturnAccountWithIdentification_WhenAccountExists() {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Mono.just(testEntity));
        when(accountMapper.toDto(any(AccountEntity.class))).thenReturn(testDTO);

        StepVerifier.create(accountService.getAccountByAccountNumber("ACC-001-SAVINGS"))
                .assertNext(account -> {
                    assertThat(account).isNotNull();
                    assertThat(account.getAccountNumber()).isEqualTo("ACC-001-SAVINGS");
                    assertThat(account.getIdentification()).isEqualTo("12345678");
                })
                .verifyComplete();
    }

    @Test
    void getAllAccounts_ShouldReturnAllAccountsWithIdentification() {
        AccountEntity secondEntity = AccountEntity.builder()
                .accountId(2L)
                .accountNumber("ACC-002-CHECKING")
                .identification("87654321")
                .accountType(AccountEntity.AccountType.CHECKING)
                .balance(new BigDecimal("2500.75"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        AccountDTO secondDTO = new AccountDTO()
                .accountId(2)
                .accountNumber("ACC-002-CHECKING")
                .identification("87654321")
                .accountType(AccountDTO.AccountTypeEnum.CHECKING)
                .balance(new BigDecimal("2500.75"));

        when(accountRepository.findAll()).thenReturn(Flux.just(testEntity, secondEntity));
        when(accountMapper.toDto(testEntity)).thenReturn(testDTO);
        when(accountMapper.toDto(secondEntity)).thenReturn(secondDTO);

        StepVerifier.create(accountService.getAllAccounts())
                .assertNext(account -> {
                    assertThat(account.getAccountId()).isEqualTo(1);
                    assertThat(account.getIdentification()).isEqualTo("12345678");
                })
                .assertNext(account -> {
                    assertThat(account.getAccountId()).isEqualTo(2);
                    assertThat(account.getIdentification()).isEqualTo("87654321");
                })
                .verifyComplete();
    }

    @Test
    void getAccountsByType_ShouldReturnAccountsOfSpecificType() {
        when(accountRepository.findByAccountType(AccountEntity.AccountType.SAVINGS))
                .thenReturn(Flux.just(testEntity));
        when(accountMapper.toDto(any(AccountEntity.class))).thenReturn(testDTO);

        StepVerifier.create(accountService.getAccountsByType("SAVINGS"))
                .assertNext(account -> {
                    assertThat(account.getAccountType()).isEqualTo(AccountDTO.AccountTypeEnum.SAVINGS);
                    assertThat(account.getIdentification()).isEqualTo("12345678");
                })
                .verifyComplete();
    }

    @Test
    void updateAccount_ShouldUpdateAccountWithIdentification_WhenAccountExists() {
        AccountDTO updatedDTO = new AccountDTO()
                .accountId(1)
                .accountNumber("ACC-001-SAVINGS")
                .identification("99999999")
                .accountType(AccountDTO.AccountTypeEnum.SAVINGS)
                .balance(new BigDecimal("2000.00"));

        AccountEntity updatedEntity = AccountEntity.builder()
                .accountId(1L)
                .accountNumber("ACC-001-SAVINGS")
                .identification("99999999")
                .accountType(AccountEntity.AccountType.SAVINGS)
                .balance(new BigDecimal("2000.00"))
                .createdAt(testEntity.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(accountRepository.findById(anyLong())).thenReturn(Mono.just(testEntity));
        when(accountMapper.toEntity(any(AccountDTO.class))).thenReturn(updatedEntity);
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(Mono.just(updatedEntity));
        when(accountMapper.toDto(any(AccountEntity.class))).thenReturn(updatedDTO);

        StepVerifier.create(accountService.updateAccount(1L, updatedDTO))
                .assertNext(account -> {
                    assertThat(account.getAccountId()).isEqualTo(1);
                    assertThat(account.getIdentification()).isEqualTo("99999999");
                    assertThat(account.getBalance()).isEqualTo(new BigDecimal("2000.00"));
                })
                .verifyComplete();
    }

    @Test
    void updateAccount_ShouldReturnEmpty_WhenAccountDoesNotExist() {
        when(accountRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(accountService.updateAccount(999L, testDTO))
                .verifyComplete();
    }

    @Test
    void deleteAccount_ShouldDeleteAccount_WhenAccountExists() {
        when(accountRepository.deleteById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(accountService.deleteAccount(1L))
                .verifyComplete();
    }

    @Test
    void createAccount_ShouldHandleBusinessAccountType() {
        AccountDTO businessDTO = new AccountDTO()
                .accountNumber("ACC-003-BUSINESS")
                .identification("11223344")
                .accountType(AccountDTO.AccountTypeEnum.BUSINESS)
                .balance(new BigDecimal("5000.00"));

        AccountEntity businessEntity = AccountEntity.builder()
                .accountId(3L)
                .accountNumber("ACC-003-BUSINESS")
                .identification("11223344")
                .accountType(AccountEntity.AccountType.BUSINESS)
                .balance(new BigDecimal("5000.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        AccountDTO savedBusinessDTO = new AccountDTO()
                .accountId(3)
                .accountNumber("ACC-003-BUSINESS")
                .identification("11223344")
                .accountType(AccountDTO.AccountTypeEnum.BUSINESS)
                .balance(new BigDecimal("5000.00"));

        when(accountMapper.toEntity(any(AccountDTO.class))).thenReturn(businessEntity);
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(Mono.just(businessEntity));
        when(accountMapper.toDto(any(AccountEntity.class))).thenReturn(savedBusinessDTO);

        StepVerifier.create(accountService.createAccount(businessDTO))
                .assertNext(account -> {
                    assertThat(account.getAccountType()).isEqualTo(AccountDTO.AccountTypeEnum.BUSINESS);
                    assertThat(account.getIdentification()).isEqualTo("11223344");
                    assertThat(account.getBalance()).isEqualTo(new BigDecimal("5000.00"));
                })
                .verifyComplete();
    }
}
