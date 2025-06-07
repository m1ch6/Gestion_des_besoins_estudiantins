package com.university.sms.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/**
 * Permet d’injecter directement le UserPrincipal courant
 * dans les méthodes des contrôleurs via :
 * 
 * @CurrentUser UserPrincipal currentUser
 */
@Target({ ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "#this")
public @interface CurrentUser {
}
