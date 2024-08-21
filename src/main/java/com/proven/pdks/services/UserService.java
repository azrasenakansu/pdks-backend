package com.proven.pdks.services;

import com.proven.pdks.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User saveUser(User user);

    User findByTCKN(String tckn);

    List<User> getAllUsers();

    List<User> saveUsers(List<User> users);

    User updateUser(String tckn, User userDetails);

    void deleteUser(String tckn);

}


