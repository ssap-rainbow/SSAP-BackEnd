package ssap.ssap.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "provider_id", unique = true)
    private String providerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "gender", nullable = true)
    private String gender;

    @Column(name = "birthdate", nullable = true)
    private String birthdate;

    @Column(name = "ageRange", nullable = true)
    private String ageRange;

    @Column(name = "profileImageUrl", nullable = true)
    private String profileImageUrl;

}