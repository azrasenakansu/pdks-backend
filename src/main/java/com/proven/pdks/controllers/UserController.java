package com.proven.pdks.controllers;

import com.proven.pdks.entities.User;
import com.proven.pdks.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/createBulk")
    public List<User> createBulk(@RequestBody List<User> resources) {
        userService.saveUsers(resources);
        return resources;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public User create(@RequestBody User resource) {
        userService.saveUser(resource);
        return resource;
    }

    @PutMapping("/update/{tckn}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User updateUser(@PathVariable String tckn, @RequestBody User userDetails) {
        return userService.updateUser(tckn, userDetails);
    }

    @DeleteMapping("/delete/{tckn}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String tckn) {
        userService.deleteUser(tckn);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/{tckn}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User searchUsers(@PathVariable String tckn) {
        return userService.findByTCKN(tckn);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

}
