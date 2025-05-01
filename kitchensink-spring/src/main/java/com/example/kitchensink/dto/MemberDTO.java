package com.example.kitchensink.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for Member entities.
 * This class is used to transfer member data between the controller and client,
 * providing a clear separation between the API layer and domain model.
 * It includes validation annotations to ensure data integrity at the API level.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    
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
    private String phoneNumber;
}
