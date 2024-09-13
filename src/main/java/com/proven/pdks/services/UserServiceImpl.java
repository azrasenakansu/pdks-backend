package com.proven.pdks.services;

import com.proven.pdks.entities.User;
import com.proven.pdks.exceptionHandling.WillfullException;
import com.proven.pdks.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User user) {
        if (!userRepository.existsById(user.getTckn())) {
            user.setPassword(passwordEncoder.encode(user.getTckn()));
            return userRepository.saveAndFlush(user);
        }
        throw new WillfullException("Bu TC Kimlik Numarasına sahip bir kullanıcı var: " + user.getTckn());
    }

    @Override
    public User findByTCKN(String tckn) {
        User user = userRepository.findByTckn(tckn);
        if (user == null) {
            throw new WillfullException("User not found with TCKN: " + tckn);
        }
        return user;
    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public List<User> saveUsers(List<User> users) {
        return userRepository.saveAllAndFlush(users);
    }

    @Override
    public User updateUser(String tckn, User userDetails) {
        Optional<User> userOptional = userRepository.findById(tckn);
        if (userOptional.isEmpty()) {
            throw new WillfullException("User not found with TCKN: " + tckn);
        }
        User user = userOptional.get();
        if (user.getTckn().equals("admin")) {
            throw new WillfullException("Bu admin hesabını değiştiremezsiniz.");
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
                throw new WillfullException("Kendi hesabınızı silemezsiniz.");
            } else if (userToDelete.getTckn().equals("admin")) {
                throw new WillfullException("Bu admin hesabını silemezsiniz.");
            }
            userRepository.delete(userToDelete);
        } else {
            throw new WillfullException("User with TCKN " + tckn + " not found.");
        }

    }

}
