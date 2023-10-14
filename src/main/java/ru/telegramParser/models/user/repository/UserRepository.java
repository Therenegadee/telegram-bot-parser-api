package ru.telegramParser.models.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.telegramParser.models.user.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByTelegramUserId(Long telegramUserId);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
