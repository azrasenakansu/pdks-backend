package com.proven.pdks.services;

import com.proven.pdks.entities.Role;
import com.proven.pdks.entities.User;
import com.proven.pdks.repositories.RoleRepository;
import com.proven.pdks.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class InitService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InitService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        if (roleRepository.count() == 0 && userRepository.count() == 0) {
            createAdminRoleAndUser();
        }
    }

    private void createAdminRoleAndUser() {
        Role adminRole = new Role();
        Role userRole = new Role();
        adminRole.setAuthority("ADMIN");
        userRole.setAuthority("USER");
        roleRepository.save(adminRole);
        roleRepository.save(userRole);

        User adminUser = new User();
        adminUser.setTckn("admin");
        adminUser.setName("Admin Account");
        adminUser.setPassword(passwordEncoder.encode("admin"));
        adminUser.setRole(adminRole);
        userRepository.save(adminUser);
    }
}
