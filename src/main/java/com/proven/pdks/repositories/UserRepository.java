package com.proven.pdks.repositories;

import com.proven.pdks.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByTckn(String tckn);

}

