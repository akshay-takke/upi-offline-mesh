package com.demo.upi_offline_mesh.controller;


import com.demo.upi_offline_mesh.dto.request.CreateAccountRequest;
import com.demo.upi_offline_mesh.dto.response.AccountResponse;
import com.demo.upi_offline_mesh.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountService.createAccount(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable Long id) {

        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {

        return ResponseEntity.ok(accountService.getAllAccounts());
    }
}
