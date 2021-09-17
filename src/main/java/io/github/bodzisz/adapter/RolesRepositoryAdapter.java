package io.github.bodzisz.adapter;

import io.github.bodzisz.enitity.Role;
import io.github.bodzisz.repository.RoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepositoryAdapter extends JpaRepository<Role, Integer>, RoleRepository {

}
