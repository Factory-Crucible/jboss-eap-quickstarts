package com.factory.kitchensink.dto;

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
 * Data Transfer Object (DTO) for creating a new Member.
 * Used for receiving member data in API requests with validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMemberDTO {
    
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
    @Digits(fraction = 0, integer = 12, message = "Phone number must be numeric and between 10 and 12 digits")
    private String phoneNumber;
}
