package j3s.qa.fintech.repo;

import j3s.qa.fintech.model.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(100);

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(String.valueOf(seq.getAndIncrement()));
        }
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}