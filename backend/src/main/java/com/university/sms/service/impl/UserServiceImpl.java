package com.university.sms.service.impl;

import com.university.sms.dto.UserCreateDTO;
import com.university.sms.dto.UserUpdateDTO;
import com.university.sms.dto.response.UserResponseDTO;
import com.university.sms.entity.AppUser;
import com.university.sms.repository.UserRepository;
import com.university.sms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDTO createUser(UserCreateDTO dto) {
        AppUser user = new AppUser();
        BeanUtils.copyProperties(dto, user);
        // Set default role if needed, e.g. user.setRole(UserRole.STUDENT);
        user.setActive(true);
        user = userRepository.save(user);
        return toDto(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> toDto((AppUser) user))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        AppUser user = (AppUser) userRepository.findById(id).orElseThrow();
        return toDto(user);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        AppUser user = (AppUser) userRepository.findById(id).orElseThrow();
        BeanUtils.copyProperties(dto, user, "id", "username");
        user = userRepository.save(user);
        return toDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserResponseDTO toDto(AppUser user) {
        UserResponseDTO dto = new UserResponseDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
