package com.clozex.shop.service;

import com.clozex.shop.model.Role;

public interface RoleService {
    Role findByName(Role.RoleName roleName);
}
