package com.example.kitchensink.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Member entity representing a registered user in the system.
 * This class uses JPA annotations for persistence and validation
 * annotations to ensure data integrity.
 */
@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name is required")
    @Size(min = 1, max = 25, message = "Name must be between 1 and 25 characters")
    private String name;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotEmpty(message = "Phone number is required")
    @Size(min = 10, max = 12, message = "Phone number must be between 10 and 12 digits")
    @Digits(fraction = 0, integer = 12, message = "Phone number must contain only digits")
    @Column(name = "phone_number")
    private String phoneNumber;
}
