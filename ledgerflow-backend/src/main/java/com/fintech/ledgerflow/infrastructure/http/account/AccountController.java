package com.fintech.ledgerflow.infrastructure.http.account;

import com.fintech.ledgerflow.application.account.AccountUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountUseCase accountUseCase;

    public AccountController(AccountUseCase accountUseCase) {
        this.accountUseCase = accountUseCase;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = AccountResponse.from(accountUseCase.create(
                request.accountNumber(), request.currency(), request.balance()));
        return ResponseEntity.created(URI.create("/api/v1/accounts/" + response.id())).body(response);
    }

    @GetMapping
    public List<AccountResponse> findAll() {
        return accountUseCase.findAll().stream().map(AccountResponse::from).toList();
    }

    @GetMapping("/{id}")
    public AccountResponse findById(@PathVariable UUID id) {
        return AccountResponse.from(accountUseCase.findById(id));
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateAccountRequest request) {
        return AccountResponse.from(accountUseCase.update(id, request.currency(), request.balance(), request.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        accountUseCase.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
