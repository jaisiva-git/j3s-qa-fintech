package j3s.qa.fintech.service;

import j3s.qa.fintech.model.Transaction;
import j3s.qa.fintech.repo.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository txRepository = new TransactionRepository();

    public Transaction record(String userId, double amount, String type, String recipientId) {
        Transaction tx = new Transaction(null, userId, amount, type, recipientId, Instant.now());
        return txRepository.save(tx);
    }

    public List<Transaction> all() {
        return txRepository.findAll();
    }
}