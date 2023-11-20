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

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "auction_end_time", nullable = false)
    private LocalDateTime auctionEndTime;

    @OneToOne(mappedBy = "errand", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ThumbNailEntity attachment;
}
