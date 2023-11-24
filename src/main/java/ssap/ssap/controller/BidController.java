package ssap.ssap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssap.ssap.dto.BidRequestDto;
import ssap.ssap.exception.InvalidAccessTokenException;
import ssap.ssap.service.BidService;
import ssap.ssap.service.OAuthService;

@CrossOrigin(origins = "*")
@Tag(name = "입찰 API", description = "입찰 관련 API")
@RestController
@RequestMapping("/api/bids")
public class BidController {
    private final BidService bidService;
    private final OAuthService oauthService;

    @Autowired
    public BidController(BidService bidService, OAuthService oauthService) {
        this.bidService = bidService;
        this.oauthService = oauthService;
    }

    @Operation(summary = "입찰하기", description = "입찰 요청을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "입찰 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 유효하지 않거나 만료됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/place")
    public ResponseEntity<String> placeBid(
            @Parameter(name = "Authorization", description = "Bearer [액세스 토큰]", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(name = "bidRequestDto", description = "입찰 요청 데이터", required = true, in = ParameterIn.DEFAULT)
            @RequestBody BidRequestDto bidRequestDto) {
        String accessToken = authorizationHeader.substring("Bearer ".length());
        if (!oauthService.isAccessTokenValid(accessToken)) {
            throw new InvalidAccessTokenException("액세스 토큰이 유효하지 않거나 만료되었습니다");
        }

        try {
            String response = bidService.placeBid(bidRequestDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "경매별 최신 입찰 조회", description = "특정 경매의 최신 입찰 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "최신 입찰 정보 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 유효하지 않거나 만료됨"),
            @ApiResponse(responseCode = "404", description = "경매 또는 입찰 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{auctionId}/latest-bid")
    public ResponseEntity<?> getLatestBid(
            @Parameter(name = "Authorization", description = "Bearer [액세스 토큰]", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(name = "auctionId", description = "경매 ID", required = true, in = ParameterIn.PATH)
            @PathVariable Long auctionId) {

        String accessToken = authorizationHeader.substring("Bearer ".length());
        if (!oauthService.isAccessTokenValid(accessToken)) {
            throw new InvalidAccessTokenException("액세스 토큰이 유효하지 않거나 만료되었습니다");
        }

        try {
            var latestBid = bidService.findLatestBidByAuctionId(auctionId);
            if (latestBid == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(latestBid);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
