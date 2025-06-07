package com.university.sms.repository;

import com.university.sms.entity.User;
import com.university.sms.entity.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository pour la gestion des utilisateurs
 */
@Repository
public interface UserRepository extends BaseRepository<User> {

    /**
     * Trouve un utilisateur par email
     * 
     * @param email email de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà
     * 
     * @param email email à vérifier
     * @return true si l'email existe
     */
    boolean existsByEmail(String email);

    /**
     * Trouve tous les utilisateurs par rôle
     * 
     * @param role rôle recherché
     * @return liste des utilisateurs
     */
    List<User> findByRole(UserRole role);

    /**
     * Trouve tous les utilisateurs actifs
     * 
     * @return liste des utilisateurs actifs
     */
    List<User> findByActiveTrue();

    /**
     * Recherche des utilisateurs par nom ou prénom
     * 
     * @param keyword mot-clé de recherche
     * @return liste des utilisateurs correspondants
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByName(@Param("keyword") String keyword);
}
