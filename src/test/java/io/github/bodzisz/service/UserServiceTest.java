package io.github.bodzisz.service;

import io.github.bodzisz.enitity.Role;
import io.github.bodzisz.enitity.User;
import io.github.bodzisz.enitity.dto.UserWriteModel;
import io.github.bodzisz.repository.RoleRepository;
import io.github.bodzisz.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Test
    @DisplayName("should add 'USER' role to user")
    void save_addsRoleUser_encodesPassword() {
        // given
        Role userRole = new Role("ROLE_USER");
        // and
        RoleRepository mockRoleRepository = mock(RoleRepository.class);
        when(mockRoleRepository.findRoleByRole("USER")).thenReturn(userRole);
        // and
        User user = new User(new UserWriteModel("foo", "bar", "password", "password"));
        // and
        PasswordEncoder mockPasswordEncoder = mock(PasswordEncoder.class);
        when(mockPasswordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        // and
        UsersRepository usersRepository = new inMemoryUsersRepository();
        // and
        UserService service = new UserService(usersRepository, mockRoleRepository, mockPasswordEncoder);


        // when
        user = service.save(user);

        // then
        assertThat(user.getRoles())
                .contains(userRole);
    }

    @Test
    @DisplayName("should encode user password")
    void save_encodesPassword() {
        // given
        RoleRepository mockRoleRepository = mock(RoleRepository.class);
        when(mockRoleRepository.findRoleByRole("USER")).thenReturn(new Role());
        // and
        User user = new User(new UserWriteModel("foo", "bar", "password", "password"));
        // and
        PasswordEncoder mockPasswordEncoder = mock(PasswordEncoder.class);
        when(mockPasswordEncoder.encode(user.getPassword())).thenReturn(user.getPassword() + "_encoded");
        // and
        UsersRepository usersRepository = new inMemoryUsersRepository();
        // and
        UserService service = new UserService(usersRepository, mockRoleRepository, mockPasswordEncoder);
        // and
        String notEncodedPassword = user.getPassword();

        // when
        user = service.save(user);

        // then
        assertThat(user.getPassword())
                .isEqualTo(notEncodedPassword + "_encoded");
    }

    private static class inMemoryUsersRepository implements UsersRepository{
        private Map<String, User> map = new HashMap<>();
        private int index = 0;

        public int count() {
            return map.values().size();
        }


        @Override
        public User findByUsername(String username) {
            return map.get(username);
        }

        @Override
        public Optional<User> findAllByUsername(String username) {
            return Optional.of(map.get(username));
        }

        @Override
        public User save(User user) {
            map.put(user.getUsername(), user);
            return user;
        }

        @Override
        public boolean existsByUsername(String username) {
            return map.containsKey(username);
        }
    }
}
