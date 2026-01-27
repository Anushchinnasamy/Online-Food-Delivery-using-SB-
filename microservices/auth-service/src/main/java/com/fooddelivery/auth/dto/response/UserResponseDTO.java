package com.fooddelivery.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fooddelivery.auth.entity.UserRole;

import java.time.LocalDateTime;

/**
 * DTO for user responses (excludes sensitive information)
 */
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private UserRole role;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_verified")
    private Boolean isVerified;

    @JsonProperty("is_email_verified")
    private Boolean isEmailVerified;

    @JsonProperty("is_phone_verified")
    private Boolean isPhoneVerified;

    @JsonProperty("last_login_at")
    private LocalDateTime lastLoginAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public UserResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public Boolean getIsEmailVerified() { return isEmailVerified; }
    public void setIsEmailVerified(Boolean isEmailVerified) { this.isEmailVerified = isEmailVerified; }

    public Boolean getIsPhoneVerified() { return isPhoneVerified; }
    public void setIsPhoneVerified(Boolean isPhoneVerified) { this.isPhoneVerified = isPhoneVerified; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
}