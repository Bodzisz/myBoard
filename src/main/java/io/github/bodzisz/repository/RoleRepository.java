package io.github.bodzisz.repository;

import io.github.bodzisz.enitity.Role;

public interface RoleRepository {

    Role findRoleByRole(String role);
}
