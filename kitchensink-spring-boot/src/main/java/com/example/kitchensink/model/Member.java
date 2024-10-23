package com.example.kitchensink.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
// TODO: Uncomment MongoDB annotations when ready to use
// import org.springframework.data.mongodb.core.mapping.Document;
// import org.springframework.data.mongodb.core.mapping.Field;

@Entity
@Table(name = "member")
// @Document(collection = "members")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.ToString(exclude = {"createdDate", "lastModifiedDate", "version"})
public class Member {

    @Id
    @GeneratedValue
    // @org.springframework.data.annotation.Id
    private Long id;

    @CreatedDate
    private java.time.LocalDateTime createdDate;

    @LastModifiedDate
    private java.time.LocalDateTime lastModifiedDate;

    @Version
    private Long version;

    @NotNull
    @Size(min = 1, max = 25)
    // @Field("name")
    private String name;

    @NotNull
    @Email
    // @Field("email")
    private String email;

    @Pattern(regexp = "^\\+?\\d{10,12}$")
    // @Field("phone_number")
    private String phoneNumber;

    @Override
    public String toString() {
        return "Member [id=" + id + ", name=" + name + ", email=" + email + ", phoneNumber=" + phoneNumber + "]";
    }
}