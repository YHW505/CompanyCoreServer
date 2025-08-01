package com.example.companycoreserver.service;

import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.dto.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    /**
     * User 엔티티를 UserInfo.Response DTO로 변환
     */
    public UserInfo.Response convertToUserInfo(User user) {
        if (user == null) {
            return null;
        }

        UserInfo.Response userInfo = new UserInfo.Response();

        try {
            // 기본 필드 매핑 (User 엔티티의 실제 필드명 사용)
            userInfo.setUserId(user.getUserId());
            userInfo.setEmployeeCode(user.getEmployeeCode());
            userInfo.setUsername(user.getUsername());
            userInfo.setEmail(user.getEmail());
            userInfo.setPhone(user.getPhone());

            // 날짜 필드 변환 (LocalDate -> String)
            if (user.getBirthDate() != null) {
                userInfo.setBirthDate(user.getBirthDate().toString());
            }

            // Enum 필드 변환 (Role enum -> String)
            if (user.getRole() != null) {
                userInfo.setRole(user.getRole().toString());
            }

            // 부서명과 직책명 설정 (관계 매핑 사용)
            userInfo.setDepartmentName(getDepartmentName(user));
            userInfo.setPositionName(getPositionName(user));

            // Integer 필드 그대로 사용 (DB TINYINT)
            userInfo.setIsFirstLogin(user.getIsFirstLogin());
            userInfo.setIsActive(user.getIsActive());

            // 생성일시 변환 (LocalDateTime -> String)
            if (user.getCreatedAt() != null) {
                userInfo.setCreatedAt(user.getCreatedAt().toString());
            }

            // 수정일시는 User 엔티티에 없으므로 null 설정
            userInfo.setUpdatedAt(null);

        } catch (Exception e) {
            System.err.println("User 변환 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return userInfo;
    }

    /**
     * 부서명 가져오기 (관계 매핑 사용)
     */
    private String getDepartmentName(User user) {
        try {
            if (user.getDepartment() != null) {
                return user.getDepartment().getDepartmentName();
            }
            return null;
        } catch (Exception e) {
            // 지연 로딩 오류나 관계 매핑 문제 시
            System.err.println("부서명 조회 오류: " + e.getMessage());
            return null;
        }
    }

    /**
     * 직책명 가져오기 (관계 매핑 사용)
     */
    private String getPositionName(User user) {
        try {
            if (user.getPosition() != null) {
                return user.getPosition().getPositionName();
            }
            return null;
        } catch (Exception e) {
            // 지연 로딩 오류나 관계 매핑 문제 시
            System.err.println("직책명 조회 오류: " + e.getMessage());
            return null;
        }
    }

    /**
     * User 엔티티를 UserInfo.ListResponse DTO로 변환 (목록용)
     */
    public UserInfo.ListResponse convertToListResponse(User user) {
        if (user == null) {
            return null;
        }

        UserInfo.ListResponse listResponse = new UserInfo.ListResponse();

        try {
            listResponse.setUserId(user.getUserId());
            listResponse.setEmployeeCode(user.getEmployeeCode());
            listResponse.setUsername(user.getUsername());
            listResponse.setEmail(user.getEmail());
            listResponse.setDepartmentName(getDepartmentName(user));
            listResponse.setPositionName(getPositionName(user));
            listResponse.setIsActive(user.getIsActive());

            if (user.getCreatedAt() != null) {
                listResponse.setCreatedAt(user.getCreatedAt().toString());
            }

        } catch (Exception e) {
            System.err.println("User ListResponse 변환 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return listResponse;
    }

    /**
     * User 엔티티를 UserInfo.LoginResponse DTO로 변환 (로그인용)
     */
    public UserInfo.LoginResponse convertToLoginResponse(User user, String accessToken, String refreshToken, Long tokenExpiresIn) {
        if (user == null) {
            return null;
        }

        UserInfo.LoginResponse loginResponse = new UserInfo.LoginResponse();

        try {
            loginResponse.setUserId(user.getUserId());
            loginResponse.setEmployeeCode(user.getEmployeeCode());
            loginResponse.setUsername(user.getUsername());
            loginResponse.setEmail(user.getEmail());
            loginResponse.setRole(user.getRole() != null ? user.getRole().toString() : null);
            loginResponse.setIsFirstLogin(user.getIsFirstLogin());
            loginResponse.setAccessToken(accessToken);
            loginResponse.setRefreshToken(refreshToken);
            loginResponse.setTokenExpiresIn(tokenExpiresIn);

        } catch (Exception e) {
            System.err.println("User LoginResponse 변환 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return loginResponse;
    }

    /**
     * UserInfo.CreateRequest DTO를 User 엔티티로 변환 (생성용)
     */
    public User convertFromCreateRequest(UserInfo.CreateRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();

        try {
            user.setEmployeeCode(request.getEmployeeCode());
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword()); // 실제로는 암호화 필요
            user.setPhone(request.getPhone());

            // 날짜 변환 (String -> LocalDate)
            if (request.getBirthDate() != null && !request.getBirthDate().isEmpty()) {
                user.setBirthDate(java.time.LocalDate.parse(request.getBirthDate()));
            }

            // Role enum 변환
            if (request.getRole() != null) {
                user.setRole(com.example.companycoreserver.entity.Enum.Role.valueOf(request.getRole()));
            }

            // 부서 ID와 직책 ID 설정
            if (request.getDepartmentId() != null) {
                user.setDepartmentId(request.getDepartmentId().intValue());
            }
            if (request.getPositionId() != null) {
                user.setPositionId(request.getPositionId().intValue());
            }

            // 기본값 설정
            user.setIsFirstLogin(1); // 첫 로그인 상태
            user.setIsActive(1);     // 활성 상태
            user.setJoinDate(java.time.LocalDate.now()); // 입사일을 현재 날짜로

        } catch (Exception e) {
            System.err.println("CreateRequest 변환 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return user;
    }

    /**
     * UserInfo.UpdateRequest로 User 엔티티 업데이트
     */
    public void updateUserFromRequest(User user, UserInfo.UpdateRequest request) {
        if (user == null || request == null) {
            return;
        }

        try {
            if (request.getUsername() != null) {
                user.setUsername(request.getUsername());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }
            if (request.getBirthDate() != null && !request.getBirthDate().isEmpty()) {
                user.setBirthDate(java.time.LocalDate.parse(request.getBirthDate()));
            }
            if (request.getDepartmentId() != null) {
                user.setDepartmentId(request.getDepartmentId().intValue());
            }
            if (request.getPositionId() != null) {
                user.setPositionId(request.getPositionId().intValue());
            }

        } catch (Exception e) {
            System.err.println("UpdateRequest 적용 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public UserInfo.Response convertToUserInfoResponse(User user) {
        if (user == null) {
            return null;
        }

        UserInfo.Response response = new UserInfo.Response();
        response.setUserId(user.getUserId());
        response.setEmployeeCode(user.getEmployeeCode());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());

        // birthDate 변환 (LocalDate -> String)
        if (user.getBirthDate() != null) {
            response.setBirthDate(user.getBirthDate().toString());
        }

        response.setRole(user.getRole().toString());

        // Department 정보
        if (user.getDepartment() != null) {
            response.setDepartmentName(user.getDepartment().getDepartmentName());
        }

        // Position 정보
        if (user.getPosition() != null) {
            response.setPositionName(user.getPosition().getPositionName());
        }

        response.setIsFirstLogin(user.getIsFirstLogin());
        response.setIsActive(user.getIsActive());

        // 날짜 변환 (LocalDateTime -> String)
        if (user.getCreatedAt() != null) {
            response.setCreatedAt(user.getCreatedAt().toString());
        }
//        if (user.getUpdatedAt() != null) {
//            response.setUpdatedAt(user.getUpdatedAt().toString());
//        }

        return response;
    }
}
