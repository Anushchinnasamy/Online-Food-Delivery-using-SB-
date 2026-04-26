package com.fooddelivery.auth.mapper;

import com.fooddelivery.auth.dto.request.RegisterRequestDTO;
import com.fooddelivery.auth.dto.response.UserResponseDTO;
import com.fooddelivery.auth.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-27T19:45:51+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDTO toResponseDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setId( user.getId() );
        userResponseDTO.setName( user.getName() );
        userResponseDTO.setEmail( user.getEmail() );
        userResponseDTO.setPhone( user.getPhone() );
        userResponseDTO.setRole( user.getRole() );
        userResponseDTO.setIsActive( user.getIsActive() );
        userResponseDTO.setIsVerified( user.getIsVerified() );
        userResponseDTO.setIsEmailVerified( user.getIsEmailVerified() );
        userResponseDTO.setIsPhoneVerified( user.getIsPhoneVerified() );
        userResponseDTO.setLastLoginAt( user.getLastLoginAt() );
        userResponseDTO.setCreatedAt( user.getCreatedAt() );
        userResponseDTO.setUpdatedAt( user.getUpdatedAt() );

        return userResponseDTO;
    }

    @Override
    public User toEntity(RegisterRequestDTO registerRequestDTO) {
        if ( registerRequestDTO == null ) {
            return null;
        }

        User user = new User();

        user.setName( registerRequestDTO.getName() );
        user.setEmail( registerRequestDTO.getEmail() );
        user.setPhone( registerRequestDTO.getPhone() );
        user.setRole( registerRequestDTO.getRole() );

        user.setIsActive( true );
        user.setIsVerified( false );
        user.setIsEmailVerified( false );
        user.setIsPhoneVerified( false );
        user.setFailedLoginAttempts( 0 );

        return user;
    }

    @Override
    public void updateEntityFromDTO(RegisterRequestDTO registerRequestDTO, User user) {
        if ( registerRequestDTO == null ) {
            return;
        }

        if ( registerRequestDTO.getName() != null ) {
            user.setName( registerRequestDTO.getName() );
        }
        if ( registerRequestDTO.getEmail() != null ) {
            user.setEmail( registerRequestDTO.getEmail() );
        }
        if ( registerRequestDTO.getPhone() != null ) {
            user.setPhone( registerRequestDTO.getPhone() );
        }
        if ( registerRequestDTO.getRole() != null ) {
            user.setRole( registerRequestDTO.getRole() );
        }
    }
}
