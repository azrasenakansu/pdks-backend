package com.proven.pdks.services;

import com.proven.pdks.entities.ExternalWorklog;
import com.proven.pdks.entities.Role;
import com.proven.pdks.entities.User;
import com.proven.pdks.exceptionHandling.ResourceNotFoundException;
import com.proven.pdks.repositories.RoleRepository;
import com.proven.pdks.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getTckn()));
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User findByTCKN(String tckn) {
        User user = userRepository.findByTckn(tckn);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with TCKN: " + tckn);
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> saveUsers(List<User> users) {
        return userRepository.saveAllAndFlush(users);
    }

    @Override
    public User updateUser(String tckn, User userDetails) {
        Optional<User> userOptional = userRepository.findById(tckn);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with TCKN: " + tckn);
        }
        User user = userOptional.get();
        if (user.getTckn().equals("admin")) {
            throw new IllegalArgumentException("Bu admin hesabını değiştiremezsiniz.");
        }
        user.setName(userDetails.getName());
        user.setRole(userDetails.getRole());
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }


    @Override
    public void deleteUser(String tckn) {
        Optional<User> userOptional = userRepository.findById(tckn);
        if (userOptional.isPresent()) {
            User userToDelete = userOptional.get();
            UserDetails currentUserDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String currentTckn = currentUserDetails.getUsername();

            if (userToDelete.getTckn().equals(currentTckn)) {
                throw new IllegalArgumentException("Kendi hesabınızı silemezsiniz.");
            } else if (userToDelete.getTckn().equals("admin")) {
                throw new IllegalArgumentException("Bu admin hesabını silemezsiniz.");
            }
            userRepository.delete(userToDelete);
        } else {
            throw new NoSuchElementException("User with TCKN " + tckn + " not found.");
        }

    }

}
