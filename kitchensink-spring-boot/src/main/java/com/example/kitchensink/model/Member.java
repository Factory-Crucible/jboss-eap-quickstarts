package com.example.kitchensink.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String name;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Size(min = 10, max = 12)
    @Digits(fraction = 0, integer = 12)
    @Pattern(regexp = "^\\d{10,12}$", message = "Phone number must be between 10 and 12 digits")
    private String phoneNumber;
}