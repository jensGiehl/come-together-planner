package de.agiehl.cometogether.domain.repository;

import de.agiehl.cometogether.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

    Optional<User> findByAccessCodeword(String accessCodeword);

    Optional<User> findByAccessCodewordIgnoreCase(String accessCodeword);
}
