package com.example.kitchensink.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Member entities.
 * This class is used for transferring member data in API requests and responses,
 * separating the internal entity model from the external API representation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class MemberDTO {
    
    /**
     * The unique identifier for the member.
     * This field is generated automatically for new members.
     */
    private Long id;
    
    /**
     * The name of the member.
     * Must not be null and must be between 1 and 25 characters.
     * Must not contain numbers.
     */
    @NotNull(message = "Name cannot be null")
    @Size(min = 1, max = 25, message = "Name must be between 1 and 25 characters")
    @Pattern(regexp = "[^0-9]*", message = "Name must not contain numbers")
    private String name;
    
    /**
     * The email address of the member.
     * Must be a valid email address and must be unique in the system.
     */
    @NotNull(message = "Email cannot be null")
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    private String email;
    
    /**
     * The phone number of the member.
     * Must be between 10 and 12 digits.
     */
    @NotNull(message = "Phone number cannot be null")
    @Size(min = 10, max = 12, message = "Phone number must be between 10 and 12 characters")
    @Digits(integer = 12, fraction = 0, message = "Phone number must contain only digits")
    private String phoneNumber;
}
