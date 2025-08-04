package com.example.companycoreserver.service;

import com.example.companycoreserver.dto.UserInfo;
import com.example.companycoreserver.dto.UserUpdateRequest;
import com.example.companycoreserver.dto.UserUpdateResponse;
import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.repository.UserRepository;
import com.example.companycoreserver.service.UserConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
public class UserUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(UserUpdateService.class);

    // 이메일 유효성 검사 패턴
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // 전화번호 유효성 검사 패턴 (한국 형식)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(010|011|016|017|018|019)-?\\d{3,4}-?\\d{4}$"
    );

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserConverter userConverter;

    /**
     * 사용자 정보 업데이트
     */
    public UserUpdateResponse updateUser(Long userId, UserUpdateRequest request) {
        try {
            logger.info("사용자 정보 업데이트 시작 - userId: {}", userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            updateBasicInfo(user, request);
            updatePassword(user, request);
            updateFirstLoginStatus(user, request);

            User updatedUser = userRepository.save(user);
            logger.info("사용자 정보 업데이트 완료 - userId: {}", userId);

            // ✅ 올바른 메서드 호출
            UserInfo.Response userInfo = userConverter.convertToUserInfo(updatedUser);

            return new UserUpdateResponse(true, "사용자 정보가 성공적으로 업데이트되었습니다.", userInfo);

        } catch (RuntimeException e) {
            logger.error("사용자 정보 업데이트 실패 - userId: {}, error: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("사용자 정보 업데이트 중 예상치 못한 오류 - userId: {}", userId, e);
            throw new RuntimeException("사용자 정보 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자 정보 조회
     */
    @Transactional(readOnly = true)
    public UserUpdateResponse getUserInfo(Long userId) {
        try {
            logger.info("사용자 정보 조회 시작 - userId: {}", userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // ✅ 올바른 메서드 호출
            UserInfo.Response userInfo = userConverter.convertToUserInfo(user);

            return new UserUpdateResponse(true, "사용자 정보 조회 성공", userInfo);

        } catch (RuntimeException e) {
            logger.error("사용자 정보 조회 실패 - userId: {}, error: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("사용자 정보 조회 중 예상치 못한 오류 - userId: {}", userId, e);
            throw new RuntimeException("사용자 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 첫 로그인 상태만 업데이트
     */
    public UserUpdateResponse updateFirstLoginStatus(Long userId) {
        try {
            logger.info("첫 로그인 상태 업데이트 시작 - userId: {}", userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            user.setIsFirstLogin(0);
            User updatedUser = userRepository.save(user);

            // ✅ 올바른 메서드 호출
            UserInfo.Response userInfo = userConverter.convertToUserInfo(updatedUser);

            logger.info("첫 로그인 상태 업데이트 완료 - userId: {}", userId);
            return new UserUpdateResponse(true, "첫 로그인 상태가 성공적으로 업데이트되었습니다.", userInfo);

        } catch (RuntimeException e) {
            logger.error("첫 로그인 상태 업데이트 실패 - userId: {}, error: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("첫 로그인 상태 업데이트 중 예상치 못한 오류 - userId: {}", userId, e);
            throw new RuntimeException("첫 로그인 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자 활성화 상태 토글
     */
    public UserUpdateResponse toggleUserActiveStatus(Long userId) {
        try {
            logger.info("사용자 활성화 상태 토글 시작 - userId: {}", userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            user.setIsActive(user.getIsActive() == 1 ? 0 : 1);
            User updatedUser = userRepository.save(user);

            // ✅ 올바른 메서드 호출
            UserInfo.Response userInfo = userConverter.convertToUserInfo(updatedUser);

            String statusMessage = user.getIsActive() == 1 ? "활성화" : "비활성화";
            logger.info("사용자 활성화 상태 토글 완료 - userId: {}, status: {}", userId, statusMessage);

            return new UserUpdateResponse(true,
                    "사용자가 성공적으로 " + statusMessage + "되었습니다.", userInfo);

        } catch (Exception e) {
            logger.error("사용자 활성화 상태 토글 중 오류 - userId: {}", userId, e);
            throw new RuntimeException("사용자 활성화 상태 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ✅ 기존 private 메서드들 (주소 업데이트 로직 추가)
    private void updateBasicInfo(User user, UserUpdateRequest request) {
        // 사용자명 업데이트
        if (isValidString(request.getUsername())) {
            String username = request.getUsername().trim();
            if (username.length() < 2 || username.length() > 50) {
                throw new RuntimeException("사용자명은 2자 이상 50자 이하로 입력해주세요.");
            }
            user.setUsername(username);
        }

        // 이메일 업데이트
        if (isValidString(request.getEmail())) {
            String email = request.getEmail().trim().toLowerCase();

            // 이메일 형식 검증
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new RuntimeException("올바른 이메일 형식이 아닙니다.");
            }

            // 이메일 중복 체크 (자신 제외)
            if (userRepository.existsByEmail(email) && !email.equals(user.getEmail())) {
                throw new RuntimeException("이미 사용 중인 이메일입니다.");
            }

            user.setEmail(email);
        }

        // 전화번호 업데이트
        if (isValidString(request.getPhone())) {
            String phone = request.getPhone().trim().replaceAll("-", "");

            // 전화번호 형식 검증
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new RuntimeException("올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)");
            }

            user.setPhone(phone);
        }

        // ✅ 주소 업데이트 추가
        if (isValidString(request.getAddress())) {
            String address = request.getAddress().trim();

            // 주소 길이 검증
            if (address.length() > 500) {
                throw new RuntimeException("주소는 500자 이하로 입력해주세요.");
            }

            user.setAddress(address);
            logger.info("주소 업데이트 완료 - userId: {}", user.getUserId());
        }

        // 생년월일 업데이트
        if (isValidString(request.getBirthDate())) {
            try {
                LocalDate birthDate = LocalDate.parse(request.getBirthDate().trim());

                // 생년월일 유효성 검사
                if (birthDate.isAfter(LocalDate.now())) {
                    throw new RuntimeException("생년월일은 현재 날짜보다 이후일 수 없습니다.");
                }

                if (birthDate.isBefore(LocalDate.of(1900, 1, 1))) {
                    throw new RuntimeException("생년월일이 너무 과거입니다.");
                }

                user.setBirthDate(birthDate);

            } catch (DateTimeParseException e) {
                throw new RuntimeException("생년월일 형식이 올바르지 않습니다. (YYYY-MM-DD 형식으로 입력해주세요)");
            }
        }
    }

    private void updatePassword(User user, UserUpdateRequest request) {
        if (!isValidString(request.getNewPassword())) {
            return; // 새 비밀번호가 없으면 업데이트하지 않음
        }

        String newPassword = request.getNewPassword().trim();

        // 첫 로그인이 아닌 경우 현재 비밀번호 확인
        if (!request.isFirstLogin()) {
            if (!isValidString(request.getCurrentPassword())) {
                throw new RuntimeException("현재 비밀번호를 입력해주세요.");
            }

            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
            }
        }

        // 비밀번호 강도 검증
        validatePasswordStrength(newPassword);

        // 새 비밀번호 설정
        user.setPassword(passwordEncoder.encode(newPassword));
        logger.info("비밀번호 업데이트 완료 - userId: {}", user.getUserId());
    }

    private void validatePasswordStrength(String password) {
        if (password.length() < 8) {
            throw new RuntimeException("비밀번호는 최소 8자 이상이어야 합니다.");
        }

        if (password.length() > 50) {
            throw new RuntimeException("비밀번호는 최대 50자 이하여야 합니다.");
        }

        // 영문, 숫자, 특수문자 포함 검사
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        if (!hasLetter || !hasDigit) {
            throw new RuntimeException("비밀번호는 영문자와 숫자를 포함해야 합니다.");
        }

        // 연속된 문자 검사 (선택사항)
        if (hasConsecutiveChars(password)) {
            throw new RuntimeException("비밀번호에 연속된 문자를 3개 이상 사용할 수 없습니다.");
        }
    }

    private boolean hasConsecutiveChars(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);

            if (c1 + 1 == c2 && c2 + 1 == c3) {
                return true; // 연속된 문자 발견
            }
        }
        return false;
    }

    private void updateFirstLoginStatus(User user, UserUpdateRequest request) {
        if (request.isFirstLogin()) {
            user.setIsFirstLogin(0); // 첫 로그인 완료로 변경 (1 -> 0)
            logger.info("첫 로그인 상태 업데이트 완료 - userId: {}", user.getUserId());
        }
    }

    private boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }

    // 🔄 부서/직급 변경
    public User updateUserDepartmentAndPosition(Long userId, Integer departmentId, Integer positionId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User userToUpdate = user.get();
            userToUpdate.setDepartmentId(departmentId);
            userToUpdate.setPositionId(positionId);
            return userRepository.save(userToUpdate);
        }
        throw new RuntimeException("사용자를 찾을 수 없습니다.");
    }

    // ✅ 주소만 별도로 업데이트하는 메서드 (선택사항)

    /**
     * 사용자 주소만 업데이트
     */
    public UserUpdateResponse updateUserAddress(Long userId, String address) {
        try {
            logger.info("사용자 주소 업데이트 시작 - userId: {}", userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 주소 유효성 검사
            if (address != null) {
                String trimmedAddress = address.trim();
                if (trimmedAddress.length() > 500) {
                    throw new RuntimeException("주소는 500자 이하로 입력해주세요.");
                }
                user.setAddress(trimmedAddress);
            } else {
                user.setAddress(null); // 주소 삭제
            }

            User updatedUser = userRepository.save(user);
            UserInfo.Response userInfo = userConverter.convertToUserInfo(updatedUser);

            logger.info("사용자 주소 업데이트 완료 - userId: {}", userId);
            return new UserUpdateResponse(true, "주소가 성공적으로 업데이트되었습니다.", userInfo);

        } catch (RuntimeException e) {
            logger.error("사용자 주소 업데이트 실패 - userId: {}, error: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("사용자 주소 업데이트 중 예상치 못한 오류 - userId: {}", userId, e);
            throw new RuntimeException("사용자 주소 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public UserUpdateResponse deleteUser(Long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return new UserUpdateResponse(false, "사용자를 찾을 수 없습니다.");
            }

            // CASCADE 하드 삭제 - 연관된 모든 데이터가 자동으로 삭제됨
            userRepository.deleteById(userId);

            return new UserUpdateResponse(true, "사용자가 성공적으로 삭제되었습니다.");

        } catch (DataIntegrityViolationException e) {
            return new UserUpdateResponse(false, "참조 무결성 제약으로 인해 삭제할 수 없습니다.");
        } catch (Exception e) {
            return new UserUpdateResponse(false, "사용자 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


}
