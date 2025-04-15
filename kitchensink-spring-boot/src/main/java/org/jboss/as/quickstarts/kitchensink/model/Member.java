package org.jboss.as.quickstarts.kitchensink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Member entity for Spring Boot application
 * Migrated from JBoss EAP kitchensink quickstart
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@JsonIgnoreProperties(ignoreUnknown = true)
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
    @Email
    private String email;

    @NotNull
    @Size(min = 10, max = 12)
    @Digits(fraction = 0, integer = 12)
    @Column(name = "phone_number")
    private String phoneNumber;
}
