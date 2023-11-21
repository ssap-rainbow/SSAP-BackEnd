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
@Table(name = "detailed_items")
public class DetailedItem {
    //ToDo: 테이블 필드 검증 추가 필요

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String detailedItemName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public DetailedItem(String detailedItemName) {
        this.detailedItemName = detailedItemName;
    }
}