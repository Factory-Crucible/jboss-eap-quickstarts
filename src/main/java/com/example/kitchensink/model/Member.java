package com.example.kitchensink.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
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
    @Email
    @Size(min = 1, max = 50)
    private String email;

    @NotNull
    @Size(min = 10, max = 12)
    @Digits(fraction = 0, integer = 12)
    @Pattern(regexp = "^\\d{10,12}$", message = "Phone number must contain only digits")
    private String phoneNumber;
}