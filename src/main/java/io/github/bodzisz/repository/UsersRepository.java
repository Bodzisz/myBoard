package io.github.bodzisz.repository;

import io.github.bodzisz.enitity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {

    User findByUsername(String username);

    Optional<User> findAllByUsername(String username);

    User save(User user);

    boolean existsByUsername(String username);
}
