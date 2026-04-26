package com.fooddelivery.auth.mapper;

import com.fooddelivery.auth.dto.request.RegisterRequestDTO;
import com.fooddelivery.auth.dto.response.UserResponseDTO;
import com.fooddelivery.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for User entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    /**
     * Convert User entity to UserResponseDTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "isVerified", source = "isVerified")
    @Mapping(target = "isEmailVerified", source = "isEmailVerified")
    @Mapping(target = "isPhoneVerified", source = "isPhoneVerified")
    @Mapping(target = "lastLoginAt", source = "lastLoginAt")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserResponseDTO toResponseDTO(User user);

    /**
     * Convert RegisterRequestDTO to User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Password will be encoded separately
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "isEmailVerified", constant = "false")
    @Mapping(target = "isPhoneVerified", constant = "false")
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "passwordChangedAt", ignore = true)
    @Mapping(target = "failedLoginAttempts", constant = "0")
    @Mapping(target = "lockedUntil", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    User toEntity(RegisterRequestDTO registerRequestDTO);

    /**
     * Update User entity from RegisterRequestDTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "isEmailVerified", ignore = true)
    @Mapping(target = "isPhoneVerified", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "passwordChangedAt", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "lockedUntil", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDTO(RegisterRequestDTO registerRequestDTO, @MappingTarget User user);
}