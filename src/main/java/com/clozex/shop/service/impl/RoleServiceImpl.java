package com.clozex.shop.service.impl;

import com.clozex.shop.exception.EntityNotFoundException;
import com.clozex.shop.model.Role;
import com.clozex.shop.model.RoleName;
import com.clozex.shop.repository.role.RoleRepository;
import com.clozex.shop.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role findByName(RoleName roleName) {
        return roleRepository.findByName(roleName).orElseThrow(() ->
                new EntityNotFoundException("Can`t find role by name" + roleName));
    }

}
