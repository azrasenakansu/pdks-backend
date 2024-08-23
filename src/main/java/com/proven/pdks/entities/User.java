package com.proven.pdks.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder
@JsonIgnoreProperties({"authorities","password","username","enabled","accountNonExpired","credentialsNonExpired","accountNonLocked"})
public class User implements UserDetails {
    @Id
    @Column(name = "tckn")
    private String tckn;
    private String name;
    private String password;
    private String email;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(role); // Returns a single role as a collection
    }

    @Override
    public String getUsername() {
        return this.tckn;
    }
}
