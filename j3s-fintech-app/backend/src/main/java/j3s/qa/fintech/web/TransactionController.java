package j3s.qa.fintech.web;

import j3s.qa.fintech.dto.ApiResponse;
import j3s.qa.fintech.dto.CreateTransactionRequest;
import j3s.qa.fintech.model.Transaction;
import j3s.qa.fintech.model.User;
import j3s.qa.fintech.service.TransactionService;
import j3s.qa.fintech.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    // POST /api/transactions
    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> create(@Valid @RequestBody CreateTransactionRequest req) {
        Optional<User> sender = userService.find(req.getUserId());
        Optional<User> recipient = userService.find(req.getRecipientId());
        if (sender.isEmpty() || recipient.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid userId or recipientId"));
        }
        if (!"transfer".equalsIgnoreCase(req.getType())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Only 'transfer' type is supported"));
        }
        // Simulate a transfer (no external systems). Reject if insufficient funds.
        if (sender.get().getBalance() < req.getAmount()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Insufficient funds"));
        }
        userService.adjustBalance(sender.get().getId(), -req.getAmount());
        userService.adjustBalance(recipient.get().getId(), req.getAmount());

        Transaction saved = transactionService.record(req.getUserId(), req.getAmount(), req.getType(), req.getRecipientId());
        return ResponseEntity.ok(ApiResponse.ok("Transfer successful (simulated)", saved));
    }

    // GET /api/transactions
    @GetMapping
    public ResponseEntity<List<Transaction>> list() {
        return ResponseEntity.ok(transactionService.all());
    }
}