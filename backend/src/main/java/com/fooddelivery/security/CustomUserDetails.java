package com.fooddelivery.security;

import com.fooddelivery.entity.User;
import com.fooddelivery.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation for Spring Security
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getIsActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive() && !user.isDeleted();
    }

    // Additional methods to access user data
    public Long getId() {
        return user.getId();
    }

    public String getName() {
        return user.getName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getPhone() {
        return user.getPhone();
    }

    public UserRole getRole() {
        return user.getRole();
    }

    public Boolean getIsVerified() {
        return user.getIsVerified();
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "id=" + user.getId() +
                ", email='" + user.getEmail() + '\'' +
                ", role=" + user.getRole() +
                ", isActive=" + user.getIsActive() +
                '}';
    }
}