package com.factory.kitchensink.mapper;

import org.springframework.stereotype.Component;

import com.factory.kitchensink.dto.CreateMemberDTO;
import com.factory.kitchensink.dto.MemberDTO;
import com.factory.kitchensink.model.Member;

/**
 * Mapper class for converting between Member entities and DTOs.
 * This class provides methods to convert from entity to DTO and vice versa.
 */
@Component
public class MemberMapper {
    
    /**
     * Converts a Member entity to a MemberDTO.
     * 
     * @param member the Member entity to convert
     * @return the corresponding MemberDTO
     */
    public MemberDTO toDTO(Member member) {
        if (member == null) {
            return null;
        }
        
        MemberDTO dto = new MemberDTO();
        dto.setId(member.getId());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        dto.setPhoneNumber(member.getPhoneNumber());
        return dto;
    }
    
    /**
     * Converts a CreateMemberDTO to a Member entity.
     * Note that this does not set the ID as it's typically generated.
     * 
     * @param dto the CreateMemberDTO to convert
     * @return the corresponding Member entity
     */
    public Member toEntity(CreateMemberDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Member member = new Member();
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setPhoneNumber(dto.getPhoneNumber());
        return member;
    }
}
