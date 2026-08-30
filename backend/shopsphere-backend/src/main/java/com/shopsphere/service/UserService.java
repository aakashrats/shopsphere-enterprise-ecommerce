package com.shopsphere.service;

import com.shopsphere.dto.request.CreateUserRequest;
import com.shopsphere.dto.response.UserResponse;

public interface UserService {

    UserResponse createdUser(CreateUserRequest request);
    UserResponse getUserById(Long id);

    void deleteUser(Long id);
}