package ssap.ssap.dto;

import lombok.Data;

@Data
public class DetailedItemDTO {
    private Long itemId;
    private String detailedItemName;

    public DetailedItemDTO(Long itemId, String detailedItemName) {
        this.itemId = itemId;
        this.detailedItemName = detailedItemName;
    }
}
