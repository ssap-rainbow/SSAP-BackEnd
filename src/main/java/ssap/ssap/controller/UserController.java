package ssap.ssap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssap.ssap.dto.Account;
import ssap.ssap.exception.CustomServiceException;
import ssap.ssap.exception.InvalidAccessTokenException;
import ssap.ssap.service.OAuthService;
import ssap.ssap.service.UserService;

import java.util.Optional;

@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "사용자 API", description = "사용자 정보 관련 API")
@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final OAuthService oauthService;

    @Autowired
    public UserController(UserService userService, OAuthService oauthService) {
        this.userService = userService;
        this.oauthService = oauthService;
    }

    @Operation(summary = "사용자 정보 조회", description = "주어진 이메일로 사용자 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 유효하지 않거나 만료되었습니다"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<Account> getUser(
            @Parameter(name = "Authorization", description = "액세스 토큰", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(name = "email", description = "조회할 사용자의 이메일", required = true, in = ParameterIn.QUERY)
            @RequestParam String email)
    {
        log.info("사용자 정보 조회 요청: {}", email);
        String accessToken = authorizationHeader.substring("Bearer ".length());
        boolean isValid = oauthService.isAccessTokenValid(accessToken);

        if (!isValid) {
            log.warn("유효하지 않거나 만료된 토큰: {}", accessToken);
            throw new InvalidAccessTokenException("액세스 토큰이 유효하지 않거나 만료되었습니다");
        }

        // 이메일 유효성 검사
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new CustomServiceException("유효하지 않은 이메일 형식입니다.", null);
        }

        Optional<Account> accountOptional = userService.getUserByEmail(email);
        if (!accountOptional.isPresent()) {
            log.warn("사용자 정보를 찾을 수 없음: {}", email);
            throw new CustomServiceException("사용자 정보를 찾을 수 없습니다.", null);
        }

        log.info("사용자 정보 조회 성공: {}", email);
        return ResponseEntity.ok(accountOptional.get());
    }
}