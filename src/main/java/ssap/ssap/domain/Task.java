package ssap.ssap.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {
    //ToDo: 테이블 필드 검증 추가 필요

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

//    @Column
//    private String location;

    @Column
    private String roadAddress;

    @Column
    private String jibunAddress;

    @Column
    private String detailedAddress;

    @Column
    private String preferredGender;

    @Column
    private String startTime;

    @Column
    private String endTime;

    @Column
    private String fee;

    @Column
    private Boolean auctionStatus;

    @Column
    private Boolean termsAgreed;

    @Column
    private String auctionStartTime;

    @Column
    private String auctionEndTime;

    @Column
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "helper_id")
    private User helper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detailed_item_id")
    private DetailedItem detailedItem;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<ThumbNailEntity> thumbNails; // Task와 ThumbNailEntity의 연관 관계 설정
}
