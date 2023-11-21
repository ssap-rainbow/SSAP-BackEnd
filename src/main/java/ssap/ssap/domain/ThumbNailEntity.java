package ssap.ssap.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "TasksAttachments")
public class ThumbNailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_data", columnDefinition = "TEXT")
    private String fileData;

    // 연관 관계 설정을 FetchType.EAGER로 설정하여 N+1 문제 방지
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id")
    private Task task; // ErrandEntity 대신 Task를 참조
}
