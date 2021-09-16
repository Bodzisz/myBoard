package io.github.bodzisz.adapter;

import io.github.bodzisz.enitity.User;
import io.github.bodzisz.repository.UsersRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepositoryAdapter extends JpaRepository<User, Integer>, UsersRepository {
}
