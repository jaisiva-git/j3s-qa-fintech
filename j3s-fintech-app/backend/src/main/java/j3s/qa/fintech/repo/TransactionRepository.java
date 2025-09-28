package j3s.qa.fintech.repo;

import j3s.qa.fintech.model.Transaction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionRepository {
    private final Map<String, Transaction> txs = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1000);

    public Transaction save(Transaction tx) {
        if (tx.getId() == null) {
            tx.setId(String.valueOf(seq.getAndIncrement()));
        }
        txs.put(tx.getId(), tx);
        return tx;
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(txs.values());
    }
}