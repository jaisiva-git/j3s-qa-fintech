package j3s.qa.fintech.service;

import j3s.qa.fintech.model.User;
import j3s.qa.fintech.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository = new UserRepository();

    public User create(String name, String email, String accountType) {
        User u = new User(null, name, email, accountType, 1000.0); // default starting balance
        return userRepository.save(u);
    }

    public Optional<User> find(String id) {
        return userRepository.findById(id);
    }

    public List<User> all() {
        return userRepository.findAll();
    }

    public void adjustBalance(String id, double delta) {
        userRepository.findById(id).ifPresent(u -> u.setBalance(u.getBalance() + delta));
    }
}