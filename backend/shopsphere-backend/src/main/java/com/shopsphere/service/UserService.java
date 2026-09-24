package com.shopsphere.service;

import com.shopsphere.dto.request.CreateUserRequest;
import com.shopsphere.dto.request.UpdateUserRequest;
import com.shopsphere.dto.response.UserResponse;

public interface UserService {

    UserResponse createdUser(CreateUserRequest request);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);


}