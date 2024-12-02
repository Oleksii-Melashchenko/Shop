package com.clozex.shop.service;

import com.clozex.shop.model.Role;
import com.clozex.shop.model.RoleName;

public interface RoleService {
    Role findByName(RoleName roleName);
}
