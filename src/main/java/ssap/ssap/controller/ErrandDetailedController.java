package ssap.ssap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssap.ssap.domain.Task;
import ssap.ssap.service.ErrandDetailedService;
import ssap.ssap.service.OAuthService;

import java.util.Map;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/errands")
@Tag(name = "심부름 상세 페이지", description = "심부름 상세 페이지 관련 API")
public class ErrandDetailedController {
    private final ErrandDetailedService errandDetailedService;
    private final OAuthService oAuthService;

    @Autowired
    public ErrandDetailedController(ErrandDetailedService errandDetailedService, OAuthService oAuthService) {
        this.errandDetailedService = errandDetailedService;
        this.oAuthService = oAuthService;
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "심부름 상세 페이지 조회")
    public ResponseEntity<?> getErrandDetails(@PathVariable Long taskId,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            boolean isValid = oAuthService.isAccessTokenValid(accessToken);
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("액세스 토큰이 유효하지 않거나 만료되었습니다.");
            }
                // 심부름 상세 페이지 조회
                Map<String, Object> errandDetails = errandDetailedService.getErrandDetails(taskId);
                return ResponseEntity.ok(errandDetails);

        }catch (EntityNotFoundException e){
            log.error("요청한 엔터티를 찾을 수 없습니다.", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("요청한 심부름 정보를 찾을 수 없습니다.");
        }catch (Exception e) {
            log.error("토큰 검증 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("토큰 검증 중 오류가 발생했습니다.");
        }
    }
}
