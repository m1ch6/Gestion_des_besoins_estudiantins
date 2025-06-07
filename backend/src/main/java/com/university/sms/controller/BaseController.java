package com.university.sms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Controller de base avec méthodes utilitaires
 */
@CrossOrigin(origins = "*", maxAge = 3600)
public abstract class BaseController {

    /**
     * Retourne une réponse OK avec données
     * 
     * @param data données à retourner
     * @return ResponseEntity
     */
    protected <T> ResponseEntity<T> ok(T data) {
        return ResponseEntity.ok(data);
    }

    /**
     * Retourne une réponse Created avec données
     * 
     * @param data données créées
     * @return ResponseEntity
     */
    protected <T> ResponseEntity<T> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    /**
     * Retourne une réponse No Content
     * 
     * @return ResponseEntity
     */
    protected ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }
}
