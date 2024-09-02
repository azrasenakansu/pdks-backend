package com.proven.pdks.services;

import com.proven.pdks.entities.User;
import com.proven.pdks.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    /***
     * bu method aslında id olan tckn ile bulucak.
     * @param tckn
     */
    @Override
    public UserDetails loadUserByUsername(String tckn) throws UsernameNotFoundException {
        User user = userRepository.findByTckn(tckn);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with name: " + tckn);
        }
        return user;
    }

    // public UserDetails loadByTckn(String tckn) {
    //     User user = userRepository.findByTckn(tckn);

    //     return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), user.getAuthorities());

    // }
}

