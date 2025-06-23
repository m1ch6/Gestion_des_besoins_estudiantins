package com.university.sms.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("APP_USER")
@NoArgsConstructor
public class AppUser extends User {
    // No extra fields for now; inherits everything from User
}
