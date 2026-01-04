package com.merchant.app.dto;

import com.merchant.app.entity.User.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private Role role;
    private String password; // Only for registration, filtered out in response usually
}
