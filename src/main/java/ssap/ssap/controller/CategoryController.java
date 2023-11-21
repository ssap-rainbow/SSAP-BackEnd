package ssap.ssap.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssap.ssap.domain.Category;
import ssap.ssap.service.CategoryService;
import ssap.ssap.service.OAuthService;

import java.util.List;

@Tag(name = "Category", description = "카테고리 관련 API")
@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;
    private final OAuthService oAuthService;

    @Autowired
    public CategoryController(CategoryService categoryService, OAuthService oAuthService){
        this.categoryService = categoryService;
        this.oAuthService = oAuthService;
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories(@RequestHeader("Authorization")String authorizationHeader){
        try {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            boolean isValid = oAuthService.isAccessTokenValid(accessToken);
            if (isValid) {
                List<Category> categories = categoryService.getCategories();
                return ResponseEntity.ok(categories);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("액세스 토큰이 유효하지 않거나 만료되었습니다.");
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("토큰 검증 중 오류가 발생했습니다.");
        }
    }
}
