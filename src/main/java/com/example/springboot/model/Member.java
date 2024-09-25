package com.example.springboot.model;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Represents a Member entity in the Spring Boot application.
 * This class is migrated from the JBoss EAP Quickstart 'kitchensink' application.
 * 
 * This entity maintains the core attributes of a Member without any direct relationships to other entities.
 * Future enhancements may include relationships such as @OneToMany or @ManyToOne as needed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String name;

    @NotNull
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotNull
    @Size(min = 10, max = 12)
    @Pattern(regexp = "^\\d+$", message = "Phone number must contain only digits")
    @Column(name = "phone_number")
    private String phoneNumber;
}