package ssap.ssap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssap.ssap.domain.Task;
import ssap.ssap.dto.ErrorField;
import ssap.ssap.dto.ErrorResponseDto;
import ssap.ssap.dto.TaskRequestDto;
import ssap.ssap.service.OAuthService;
import ssap.ssap.service.TaskService;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@Tag(name = "심부름 요청서 작성 API", description = "Swagger 테스트용 심부름 요청서 작성 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RequestController {

    private final TaskService taskService;
    private final OAuthService oauthService;

    @Operation(summary = "Errand_Post Create", description = "게시글 생성을 위한 심부름 요청서를 작성한다.")
    @PostMapping("/request")
    public ResponseEntity<?> createErrandRequestForm(
            @RequestHeader("Authorization") String authorizationHeader,
            @Validated @ModelAttribute TaskRequestDto.CreateForm request, BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors()) {
            List<ErrorField> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> new ErrorField(error.getField()))
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(ErrorResponseDto.error("입력 값 검증 오류", errors));
        }

        try {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            boolean isValid = oauthService.isAccessTokenValid(accessToken);
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("액세스 토큰이 유효하지 않거나 만료되었습니다.");
            }

            // 심부름 요청 생성
            Task task = taskService.createPost(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new TaskRequestDto.CreateFormResponse("심부름 요청이 생성되었습니다.", task));

        } catch (Exception e) {
            log.error("Exception e", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponseDto.error("요청을 처리하는 중에 서버에서 오류가 발생했습니다.", ""));
        }
    }
}
