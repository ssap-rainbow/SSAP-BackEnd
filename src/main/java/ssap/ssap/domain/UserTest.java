package ssap.ssap.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "users_test")
public class UserTest {
    //ToDo: 테이블 필드 검증 추가 필요

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

//    @NotBlank
//    @Length(min = 1, max = 50, message = "이름은 최대 50자까지 쓸 수 있습니다.")
    @Column
    private String name;

//    @NotBlank
//    @Pattern(regexp = "^[A-Za-z0-9]+[A-Za-z0-9._%+-]*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일 형식을 확인해주세요.")
    @Column
    private String email;

    //두가지 값 적용할 수 있는 타입 설정 필요
//    @NotBlank
    @Column
    private String gender;

//    @NotBlank
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column
    private String birthdate;

//    @NotEmpty
//    private String password;

//    @NotBlank
//    @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$", message = "휴대폰 번호를 확인해주세요.")
    @Column
    private String phoneNumber;

//    @NotBlank
    @Column
    private Boolean isVerified;

//    @NotBlank
    @Column
    private Boolean hasAgreedToTerms;

    //URL(?) 검증 필요
    @Column
    private String profilePicture;

    //소수점 검증 및 비지니스 로직 필요
    @Column
    private String rating;

//    private String createdAt;
//    private String updatedAt;

    @Column
    private String roadAddress;

    @Column
    private String jibunAddress;

    public UserTest(String name, String email, String gender, String birthdate, String phoneNumber, Boolean isVerified, Boolean hasAgreedToTerms,
                    String profilePicture, String rating, String roadAddress, String jibunAddress) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.birthdate = birthdate;
        this.phoneNumber = phoneNumber;
        this.isVerified = isVerified;
        this.hasAgreedToTerms = hasAgreedToTerms;
        this.profilePicture = profilePicture;
        this.rating = rating;
        this.roadAddress = roadAddress;
        this.jibunAddress = jibunAddress;
    }
}
