package com.university.sms.service;

import com.university.sms.entity.BaseEntity;
import com.university.sms.exception.ResourceNotFoundException;
import com.university.sms.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de base générique
 * 
 * @param <T> Type de l'entité
 * @param <R> Type du repository
 */
@Transactional
public abstract class BaseService<T extends BaseEntity, R extends BaseRepository<T>> {

    protected final R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    /**
     * Trouve une entité par ID
     * 
     * @param id identifiant
     * @return entité trouvée
     * @throws ResourceNotFoundException si non trouvée
     */
    public T findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName() + " non trouvé avec l'ID: " + id));
    }

    /**
     * Trouve toutes les entités
     * 
     * @return liste de toutes les entités
     */
    public List<T> findAll() {
        return repository.findAll();
    }

    /**
     * Trouve toutes les entités avec pagination
     * 
     * @param pageable informations de pagination
     * @return page d'entités
     */
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Sauvegarde une entité
     * 
     * @param entity entité à sauvegarder
     * @return entité sauvegardée
     */
    public T save(T entity) {
        return repository.save(entity);
    }

    /**
     * Supprime une entité par ID
     * 
     * @param id identifiant
     */
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(getEntityName() + " non trouvé avec l'ID: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Vérifie si une entité existe
     * 
     * @param id identifiant
     * @return true si existe
     */
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    /**
     * Retourne le nom de l'entité pour les messages d'erreur
     * 
     * @return nom de l'entité
     */
    protected abstract String getEntityName();
}
