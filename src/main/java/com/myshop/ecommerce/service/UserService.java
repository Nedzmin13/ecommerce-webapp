package com.myshop.ecommerce.service;

import com.myshop.ecommerce.entity.User;
import com.myshop.ecommerce.enums.AuthProvider;

import java.util.Optional;

public interface UserService {

    User registerNewUser(String username, String email, String password, String firstName, String lastName);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    boolean checkCurrentPassword(Long userId, String currentPassword);


    User processOAuthPostLogin(String email, String displayNameFromProvider, String providerId, AuthProvider provider, String firstNameFromProvider, String lastNameFromProvider);

    User updateUserProfile(Long userId, String firstName, String lastName);

    void updateUserPassword(Long userId, String newPassword);




}