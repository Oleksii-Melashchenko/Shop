package com.clozex.shop.repository.role;

import com.clozex.shop.model.Role;
import com.clozex.shop.model.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
