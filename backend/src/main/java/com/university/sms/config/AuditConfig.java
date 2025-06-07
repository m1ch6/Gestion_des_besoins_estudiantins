package com.university.sms.config;

import com.university.sms.security.UserPrincipal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Active l’audit automatique pour les entités héritant de BaseEntity
 * (champs createdAt / updatedAt). Fournit également un AuditorAware
 * qui tente de remonter l’id de l’utilisateur courant afin de le
 * stocker dans les colonnes “created_by / modified_by” si vous les
 * ajoutez plus tard.
 */
@Configuration
@EnableJpaAuditing
public class AuditConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal p) {
                return Optional.ofNullable(p.getId());
            }
            return Optional.empty();
        };
    }
}
