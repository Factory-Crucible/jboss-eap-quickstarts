package com.factory.kitchensink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for Member entity.
 * Used for transferring member data in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
}
