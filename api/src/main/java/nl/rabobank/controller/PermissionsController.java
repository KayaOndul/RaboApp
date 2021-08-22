package nl.rabobank.controller;


import lombok.RequiredArgsConstructor;
import nl.rabobank.model.AccountQueryResponse;
import nl.rabobank.model.GrantAccessRequest;
import nl.rabobank.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("permissions")
@RequiredArgsConstructor
public class PermissionsController {
    private final CustomerService  customerService;

    @GetMapping
    public ResponseEntity<Void> get() {
        return ResponseEntity.ok().build();

    }
    @PostMapping
    public ResponseEntity<Void> grantAccess(@RequestBody GrantAccessRequest grantAccessRequest) {
        customerService.grantAccessToUser(grantAccessRequest);
        return ResponseEntity.accepted().build();

    }
//
//    @GetMapping
//    public ResponseEntity<AccountQueryResponse> getGrantedAccounts(@RequestParam Long granteeId) {
//        AccountQueryResponse accountQueryResponse = customerService.getGrantsOfUser(granteeId);
//        return ResponseEntity.ok(accountQueryResponse);
//    }
}
