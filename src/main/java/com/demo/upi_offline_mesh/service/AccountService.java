package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.dto.request.CreateAccountRequest;
import com.demo.upi_offline_mesh.dto.response.AccountResponse;
import com.demo.upi_offline_mesh.entity.Account;
import com.demo.upi_offline_mesh.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        if (accountRepository.existsByAccountNumber(request.accountNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Account number already exists");
        }

        Account account = new Account();
        account.setAccountNumber(request.accountNumber());
        account.setAccountHolderName(request.accountHolderName());
        account.setBalance(BigDecimal.ZERO);

        try {
            Account saved = accountRepository.saveAndFlush(account);
            return toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Account could not be created; check unique constraints",
                    ex
            );
        }
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found"
                ));

        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {

        return accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AccountResponse toResponse(Account account) {

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountHolderName(),
                account.getBalance(),
                account.getStatus(),
                account.getCreatedAt()
        );
    }
}
