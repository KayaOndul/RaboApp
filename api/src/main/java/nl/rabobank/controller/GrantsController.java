package nl.rabobank.controller;


import lombok.RequiredArgsConstructor;
import nl.rabobank.account.AccountQueryResponse;
import nl.rabobank.authorizations.GrantAccessRequest;
import nl.rabobank.service.impl.RabobankGrantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("grant")
@RequiredArgsConstructor
public class GrantsController {
    private final RabobankGrantService rabobankGrantService;


    @PostMapping
    public ResponseEntity<Void> grantAccess(@RequestBody GrantAccessRequest grantAccessRequest) {
        rabobankGrantService.grantAccessToUser(grantAccessRequest);
        return ResponseEntity.accepted().build();

    }

    @GetMapping
    public ResponseEntity<AccountQueryResponse> getGrantedAccounts(@RequestParam String granteeId) {
        AccountQueryResponse accountQueryResponse = rabobankGrantService.getGrantsOfUser(granteeId);
        return ResponseEntity.ok(accountQueryResponse);
    }
}
