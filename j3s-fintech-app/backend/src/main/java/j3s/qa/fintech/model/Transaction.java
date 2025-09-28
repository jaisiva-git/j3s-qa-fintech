package j3s.qa.fintech.model;

import java.time.Instant;

public class Transaction {
    private String id;
    private String userId;
    private double amount;
    private String type; // e.g. "transfer"
    private String recipientId;
    private Instant createdAt;

    public Transaction() {}

    public Transaction(String id, String userId, double amount, String type, String recipientId, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.recipientId = recipientId;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}