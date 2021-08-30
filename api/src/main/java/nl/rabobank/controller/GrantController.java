package nl.rabobank.controller;


import lombok.RequiredArgsConstructor;
import nl.rabobank.account.AccountQueryResponse;
import nl.rabobank.authorizations.GrantAccessRequest;
import nl.rabobank.service.impl.RabobankGrantService;
import nl.rabobank.service.spec.GrantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("grant")
@RequiredArgsConstructor
public class GrantController {
    private final GrantService rabobankGrantService;


    @PostMapping
    public ResponseEntity<Void> grantAccess(@RequestBody @Valid GrantAccessRequest grantAccessRequest) {
        rabobankGrantService.grantAccessToUser(grantAccessRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<AccountQueryResponse> getGrantedAccounts(@RequestParam String granteeId) {
        AccountQueryResponse accountQueryResponse = rabobankGrantService.getGrantsOfUser(granteeId);
        return ResponseEntity.ok(accountQueryResponse);
    }
}
