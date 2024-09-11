package com.project.springboot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Member entity in the application.
 * This class is annotated with JPA annotations for database persistence
 * and Lombok annotations to reduce boilerplate code.
 */
@Document(collection = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    private String id;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String name;

    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotNull
    @Size(min = 10, max = 12)
    @Digits(fraction = 0, integer = 12)
    @Pattern(regexp = "^\\d{10,12}$", message = "Phone number must be between 10 and 12 digits")
    private String phoneNumber;

    /**
     * Default constructor for JPA and Lombok.
     * Use of @NoArgsConstructor annotation.
     */

    /**
     * Full constructor with all fields.
     * Use of @AllArgsConstructor annotation.
     */

    /**
     * Getter and Setter methods for all fields.
     * Use of @Data annotation from Lombok.
     */
}