package ssap.ssap.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Tasks")
public class ErrandEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column
    private LocalDateTime auctionStartTime;

    @Column
    private LocalDateTime auctionEndTime;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ThumbNailEntity attachment; // Task 필드를 참조하도록 수정
}
