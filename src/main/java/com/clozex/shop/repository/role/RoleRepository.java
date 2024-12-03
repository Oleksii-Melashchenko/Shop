package com.clozex.shop.repository.role;

import com.clozex.shop.model.Role;
import com.clozex.shop.model.RoleName;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByName(RoleName roleName);
}
