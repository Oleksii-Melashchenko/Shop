package com.clozex.shop.service.impl;

import com.clozex.shop.dto.user.UserRegistrationRequestDto;
import com.clozex.shop.dto.user.UserResponseDto;
import com.clozex.shop.exception.RegistrationException;
import com.clozex.shop.mapper.UserMapper;
import com.clozex.shop.model.Role;
import com.clozex.shop.model.User;
import com.clozex.shop.repository.user.UserRepository;
import com.clozex.shop.service.RoleService;
import com.clozex.shop.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        String email = requestDto.email();
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("User with email: " + email + " already exists");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(roleService.findByName(Role.RoleName.ROLE_USER)));
        return userMapper.toDto(userRepository.save(user));
    }
}
