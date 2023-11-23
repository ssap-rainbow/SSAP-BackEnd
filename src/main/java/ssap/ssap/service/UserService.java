package ssap.ssap.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ssap.ssap.domain.User;
import ssap.ssap.dto.Account;
import ssap.ssap.exception.CustomServiceException;
import ssap.ssap.repository.UserRepository;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<Account> getUserByEmail(String email) {
        log.debug("사용자 이메일로 조회: {}", email);

        if (email == null || email.trim().isEmpty()) {
            log.warn("이메일이 null 또는 빈 문자열: {}", email);
            throw new IllegalArgumentException("이메일은 null이거나 빈 값일 수 없습니다.");
        }

        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                log.warn("사용자 정보를 찾을 수 없음: {}", email);
                return Optional.empty();
            }

            User user = userOptional.get();
            Account account = new Account();
            account.setUserName(user.getName());
            account.setUserEmail(user.getEmail());
            account.setGender(user.getGender());
            account.setBirthdate(user.getBirthdate());
            account.setAgeRange(user.getAgeRange());
            account.setProfileImageUrl(user.getProfileImageUrl());

            log.debug("사용자 정보 조회 완료: {}", email);
            return Optional.of(account);

        } catch (DataAccessException e) {
            log.error("데이터베이스 접근 중 오류 발생: {}", email, e);
            throw new CustomServiceException("데이터베이스 접근 중 오류 발생", e);
        }
    }
}
