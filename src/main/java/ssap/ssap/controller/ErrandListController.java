package ssap.ssap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssap.ssap.service.ErrandListService;
import ssap.ssap.service.OAuthService;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/errands")
@Tag(name = "심부름 리스트 상세 페이지", description = "심부름 리스트 상세 페이지 관련 API")
public class ErrandListController {
    private final ErrandListService errandListService;
    private final OAuthService oAuthService;

    @Autowired
    public ErrandListController(ErrandListService errandListService, OAuthService oAuthService) {
        this.errandListService = errandListService;
        this.oAuthService = oAuthService;
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "심부름 리스트 상세 페이지 조회")
    public ResponseEntity<?> getErrandsByCategory(@PathVariable Long categoryId,
                                              @RequestHeader("Authorization") String authorizationHeader) {

        try {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            boolean isValid = oAuthService.isAccessTokenValid(accessToken);
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("액세스 토큰이 유효하지 않거나 만료되었습니다.");
            }

            List<Map<String, Object>> errands = errandListService.getErrandsByCategory(categoryId);
            return ResponseEntity.ok(errands);

        }catch (EntityNotFoundException e){
            log.error("요청한 엔터티를 찾을 수 없습니다.", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("요청한 심부름 정보를 찾을 수 없습니다.");
        }catch (Exception e) {
            log.error("Exception e", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("요청을 처리하는 중에 서버에서 오류가 발생했습니다.");
        }
    }
}