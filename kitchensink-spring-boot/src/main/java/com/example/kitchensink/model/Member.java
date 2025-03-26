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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Member entity representing a person in the application.
 * This class is a JPA entity that maps to the "members" table.
 */
@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String name;

    @NotNull
    @NotEmpty
    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email;

    @NotNull
    @Size(min = 10, max = 12)
    @Digits(fraction = 0, integer = 12, message = "Phone number must contain only digits")
    @Column(name = "phone_number")
    private String phoneNumber;
}
