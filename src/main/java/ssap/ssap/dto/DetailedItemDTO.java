package ssap.ssap.dto;

import lombok.Data;

@Data
public class DetailedItemDTO {
    private Long id;
    private String categoryName;

    public DetailedItemDTO(Long id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }
}
