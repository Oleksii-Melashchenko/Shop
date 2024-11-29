package com.clozex.shop.mapper;

import com.clozex.shop.config.MapperConfig;
import com.clozex.shop.dto.user.UserRegistrationRequestDto;
import com.clozex.shop.dto.user.UserResponseDto;
import com.clozex.shop.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserResponseDto responseDto);

    User toModel(UserRegistrationRequestDto requestDto);
}
