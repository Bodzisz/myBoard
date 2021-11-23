package io.github.bodzisz.service;

import io.github.bodzisz.enitity.User;
import io.github.bodzisz.repository.RoleRepository;
import io.github.bodzisz.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {
    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UsersRepository usersRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User user) {
        if(user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        user.getRoles().add(roleRepository.findRoleByRole("USER"));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    public User findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return usersRepository.existsByUsername(username);
    }
}
