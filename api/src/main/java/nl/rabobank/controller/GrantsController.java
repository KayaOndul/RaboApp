package nl.rabobank.controller;


import lombok.RequiredArgsConstructor;
import nl.rabobank.model.account.AccountQueryResponse;
import nl.rabobank.model.authorization.GrantAccessRequest;
import nl.rabobank.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("grant")
@RequiredArgsConstructor
public class GrantsController {
    private final CustomerService customerService;


    @PostMapping
    public ResponseEntity<Void> grantAccess(@RequestBody GrantAccessRequest grantAccessRequest) {
        customerService.grantAccessToUser(grantAccessRequest);
        return ResponseEntity.accepted().build();

    }

    @GetMapping
    public ResponseEntity<AccountQueryResponse> getGrantedAccounts(@RequestParam String granteeId) {
        AccountQueryResponse accountQueryResponse = customerService.getGrantsOfUser(granteeId);
        return ResponseEntity.ok(accountQueryResponse);
    }
}
