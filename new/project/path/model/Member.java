package new.project.path.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "Member", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    @Column(nullable = false)
    private String name;

    @NotNull
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Pattern(regexp = "\\d{10,12}", message = "Phone number must be between 10 and 12 digits")
    @Column(nullable = false)
    private String phoneNumber;

    public record MemberRecord(
            @NotNull @Size(min = 1, max = 25) @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers") String name,
            @NotNull @Email String email,
            @NotNull @Pattern(regexp = "\\d{10,12}", message = "Phone number must be between 10 and 12 digits") String phoneNumber) {
        public Member toMember() {
            return new Member(null, name, email, phoneNumber);
        }
    }

    public static MemberRecord fromMember(Member member) {
        return new MemberRecord(member.getName(), member.getEmail(), member.getPhoneNumber());
    }
}