package com.proven.pdks.services;

import com.proven.pdks.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User saveUser(User user);

    User findByTCKN(String tckn);

    Page<User> getAllUsers(int page, int size);

    List<User> saveUsers(List<User> users);

    User updateUser(String tckn, User userDetails);

    void deleteUser(String tckn);

}


