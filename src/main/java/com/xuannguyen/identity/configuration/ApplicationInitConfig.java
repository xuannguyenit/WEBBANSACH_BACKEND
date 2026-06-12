package com.xuannguyen.identity.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.xuannguyen.identity.constant.PredefinedRole;
import com.xuannguyen.identity.entity.Role;
import com.xuannguyen.identity.entity.User;
import com.xuannguyen.identity.repository.RoleRepository;
import com.xuannguyen.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";
    @NonFinal
    static final String ADMIN_CODE = "ADMIN";
    @NonFinal
    static final String ROLE_CODE = "ADMIN";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring.datasource",
            value = "driver-class-name",
            havingValue = "org.postgresql.Driver")
    ApplicationRunner applicationRunner(
            UserRepository userRepository,
            RoleRepository roleRepository) {

        return args -> {

            if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {

                roleRepository.findByCode(PredefinedRole.USER_ROLE_CODE)
                        .orElseGet(() ->
                                roleRepository.save(Role.builder()
                                        .name(PredefinedRole.USER_ROLE)
                                        .code(PredefinedRole.USER_ROLE_CODE)
                                        .description("User role")
                                        .build()));

                Role adminRole = roleRepository.findByCode(PredefinedRole.ADMIN_ROLE_CODE)
                        .orElseGet(() ->
                                roleRepository.save(Role.builder()
                                        .name(PredefinedRole.ADMIN_ROLE)
                                        .code(PredefinedRole.ADMIN_ROLE_CODE)
                                        .description("Admin role")
                                        .build()));

                var roles = new HashSet<Role>();
                roles.add(adminRole);

                User user = User.builder()
                        .code("ADMIN")
                        .username("admin")
                        .email("admin@ecommercebook.com")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();

                userRepository.save(user);

                log.warn("Admin user created successfully");
            }
        };
    }
//    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
//        log.info("Initializing application.....");
//        return args -> {
//            if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
//                roleRepository.save(Role.builder()
//                        .name(PredefinedRole.USER_ROLE)
//                        .code(PredefinedRole.USER_ROLE_CODE)
//                        .description("User role")
//                        .build());
//
//                Role adminRole = roleRepository.save(Role.builder()
//                        .name(PredefinedRole.ADMIN_ROLE)
//                        .code(PredefinedRole.ADMIN_ROLE_CODE)
//                        .description("Admin role")
//                        .build());
//
//                var roles = new HashSet<Role>();
//                roles.add(adminRole);
//
//                User user = User.builder()
//                        .username(ADMIN_USER_NAME)
//
//                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
//                        .roles(roles)
//                        .build();
//
//                userRepository.save(user);
//                log.warn("admin user has been created with default password: admin, please change it");
//            }
//            log.info("Application initialization completed .....");
//        };
//    }
}
