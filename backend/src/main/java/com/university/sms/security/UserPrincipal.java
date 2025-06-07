package com.university.sms.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.university.sms.entity.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Wrapper léger autour de l’entité User afin de ne jamais
 * envoyer cette dernière dans la réponse JSON.
 *
 * NB : l’entité User implémente déjà UserDetails ; nous pourrions
 * l’utiliser telle quelle, mais pour plus de contrôle (et afin
 * d’éviter d’exposer les champs JPA dans le token) nous préférons
 * un « view model » dédié.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class UserPrincipal implements UserDetails {

    private Long id;
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;

    /* ------------------------------------------------------------------ */
    /* Factory method à partir de User */
    /* ------------------------------------------------------------------ */
    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities(), // déjà construit dans l’entité
                user.isEnabled());
    }

    /* === Implémentation UserDetails === */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
