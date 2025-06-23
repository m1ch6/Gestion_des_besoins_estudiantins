package com.university.sms.service;

import com.university.sms.dto.UserCreateDTO;
import com.university.sms.dto.UserUpdateDTO;
import com.university.sms.dto.response.UserResponseDTO;
import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserCreateDTO dto);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long id);

    UserResponseDTO updateUser(Long id, UserUpdateDTO dto);

    void deleteUser(Long id);
}
