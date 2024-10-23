package com.example.kitchensink.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "Name is required")
    @Size(min = 1, max = 25, message = "Name must be between 1 and 25 characters")
    private String name;

    @NotNull(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^\\+?\\d{10,12}$", message = "Phone number must be valid")
    private String phoneNumber;
}