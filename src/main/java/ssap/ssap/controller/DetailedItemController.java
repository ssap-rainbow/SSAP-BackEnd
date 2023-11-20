package ssap.ssap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssap.ssap.dto.DetailedItemDTO;
import ssap.ssap.service.DetailedItemService;
import ssap.ssap.service.OAuthService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/categories")
@Tag(name = "카테고리 상세 목록 조회 입니다.", description = "카테고리 상세 목록 조회 API EndPoint 입니다.")
public class DetailedItemController {
    private final DetailedItemService detailItemService;
    private final OAuthService oAuthService;

    @Autowired
    public DetailedItemController(DetailedItemService detailItemService, OAuthService oAuthService) {
        this.detailItemService = detailItemService;
        this.oAuthService = oAuthService;
    }

    @Operation(summary = "카테고리별로 세부 항목 가져오기",
            description = "지정된 카테고리에 대한 세부 항목 목록을 반환합니다."
    )
    @GetMapping("/{categoryId}/items")
    public ResponseEntity<?> getDetailItemsByCategory(
            @Parameter(description = "항목을 가져올 카테고리의 ID", required = true)
            @PathVariable Long categoryId,
            @Parameter(description = "Authorization token")
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader) {
        try {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            boolean isValid = oAuthService.isAccessTokenValid(accessToken);
        if (isValid) {
            List<DetailedItemDTO> detailItems = detailItemService.findAllDetailItemsByCategoryId(categoryId);
            return ResponseEntity.ok(detailItems);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("액세스 토큰이 유효하지 않거나 만료되었습니다.");
        }
    } catch (Exception e) {
            log.error("토큰 검증 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("토큰 검증 중 오류가 발생했습니다.");
        }
    }
}
