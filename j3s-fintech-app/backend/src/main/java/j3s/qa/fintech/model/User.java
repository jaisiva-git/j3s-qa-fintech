package j3s.qa.fintech.model;

public class User {
    private String id;
    private String name;
    private String email;
    private String accountType;
    private double balance;

    public User() {}

    public User(String id, String name, String email, String accountType, double balance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}