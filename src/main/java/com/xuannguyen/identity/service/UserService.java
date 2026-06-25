package com.xuannguyen.identity.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.xuannguyen.event.dto.NotificationEvent;
import com.xuannguyen.identity.constant.PredefinedRole;
import com.xuannguyen.identity.dto.request.UserCreationRequest;
import com.xuannguyen.identity.dto.request.UserUpdateRequest;
import com.xuannguyen.identity.dto.response.UserResponse;
import com.xuannguyen.identity.entity.Role;
import com.xuannguyen.identity.entity.User;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.mapper.UserMapper;
import com.xuannguyen.identity.repository.RoleRepository;
import com.xuannguyen.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    KafkaTemplate<String, Object> kafkaTemplate;

public UserResponse createUser(UserCreationRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
        throw new AppException(ErrorCode.USER_EXISTED);
    }

    if (userRepository.existsByEmail(request.getEmail())) {
        throw new AppException(ErrorCode.EMAIL_EXISTED);
    }

    String code = request.getUsername();

    HashSet<Role> roles = new HashSet<>();

    Role role = roleRepository.findByCode(PredefinedRole.USER_ROLE_CODE)
            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

    roles.add(role);

    User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .email(request.getEmail())
            .code(code)
            .roles(roles)
            .build();

    User savedUser = userRepository.save(user);

    NotificationEvent notificationEvent = NotificationEvent.builder()
            .channel("EMAIL")
            .recipient(savedUser.getEmail())
            .templateCode("USER_CREATED")
            .subject("Welcome to My Shop")
            .body("Hello, " + savedUser.getUsername())
            .build();

    kafkaTemplate.send("notification-delivery", notificationEvent);

    return userMapper.toUserResponse(savedUser);
}

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }


    public UserResponse getUser(Long id) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role ->
                        PredefinedRole.ADMIN_ROLE_CODE.equals(role.getCode()));

        // Không phải admin và đang xem user khác
        if (!isAdmin && !currentUser.getId().equals(id)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }
}
