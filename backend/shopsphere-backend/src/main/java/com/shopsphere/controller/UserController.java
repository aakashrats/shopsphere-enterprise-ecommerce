package com.shopsphere.controller;

import com.shopsphere.dto.request.CreateUserRequest;
import com.shopsphere.dto.request.UpdateUserRequest;
import com.shopsphere.dto.response.UserResponse;
import com.shopsphere.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request ) {

        UserResponse response = userService.createdUser(request);

        return  ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    )
    {
        UserResponse response = userService.getUserById(id);

        return  ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    )
    {
        UserResponse response = userService.updateUser(id, request);

        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id)
    {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}
