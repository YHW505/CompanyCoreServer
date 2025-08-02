package com.example.companycoreserver.service;

import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.dto.UserInfo;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
            // 기본 필드 매핑
            userInfo.setUserId(user.getUserId());
            userInfo.setEmployeeCode(user.getEmployeeCode());
            userInfo.setUsername(user.getUsername());
            userInfo.setEmail(user.getEmail());
            userInfo.setPhone(user.getPhone());

            // 날짜 필드 (LocalDate 그대로 사용)
            userInfo.setBirthDate(user.getBirthDate());
            userInfo.setJoinDate(user.getJoinDate()); // ✅ 추가

            // Enum 필드 변환 (Role enum -> String)
            if (user.getRole() != null) {
                userInfo.setRole(user.getRole().toString());
            }

            // ID 필드 추가 ✅
            if (user.getDepartmentId() != null) {
                userInfo.setDepartmentId(Long.valueOf(user.getDepartmentId()));
            }
            if (user.getPositionId() != null) {
                userInfo.setPositionId(Long.valueOf(user.getPositionId()));
            }

            // 부서명과 직책명 설정 (관계 매핑 사용)
            userInfo.setDepartmentName(getDepartmentName(user));
            userInfo.setPositionName(getPositionName(user));

            // Boolean 필드로 변환 ✅
            userInfo.setIsFirstLogin(convertIntegerToBoolean(user.getIsFirstLogin()));
            userInfo.setIsActive(convertIntegerToBoolean(user.getIsActive()));

            // 생성일시 (LocalDateTime 그대로 사용) ✅
            userInfo.setCreatedAt(user.getCreatedAt());

        } catch (Exception e) {
            System.err.println("User 변환 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return userInfo;
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
            listResponse.setJoinDate(user.getJoinDate()); // ✅ 추가
            listResponse.setEmail(user.getEmail());
            listResponse.setDepartmentName(getDepartmentName(user));
            listResponse.setPositionName(getPositionName(user));
            listResponse.setIsActive(convertIntegerToBoolean(user.getIsActive())); // ✅ Boolean 변환
            listResponse.setCreatedAt(user.getCreatedAt()); // ✅ LocalDateTime 그대로

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
            loginResponse.setIsFirstLogin(convertIntegerToBoolean(user.getIsFirstLogin())); // ✅ Boolean 변환
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

            // 날짜 필드 (LocalDate 그대로 사용) ✅
            user.setBirthDate(request.getBirthDate());
            user.setJoinDate(request.getJoinDate() != null ? request.getJoinDate() : LocalDate.now()); // ✅

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
            // LocalDate 그대로 사용 ✅
            if (request.getBirthDate() != null) {
                user.setBirthDate(request.getBirthDate());
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

    /**
     * User 엔티티를 UserInfo.Response DTO로 변환 (별칭 메서드)
     */
    public UserInfo.Response convertToUserInfoResponse(User user) {
        return convertToUserInfo(user); // 기존 메서드 재사용
    }

    // ========== 헬퍼 메서드들 ==========

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
     * Integer를 Boolean으로 변환 (DB TINYINT -> Boolean) ✅
     */
    private Boolean convertIntegerToBoolean(Integer value) {
        if (value == null) {
            return null;
        }
        return value == 1;
    }

    /**
     * Boolean을 Integer로 변환 (Boolean -> DB TINYINT) ✅
     */
    private Integer convertBooleanToInteger(Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? 1 : 0;
    }

    /**
     * StatusChangeRequest를 User 엔티티에 적용 ✅
     */
    public void updateUserStatus(User user, UserInfo.StatusChangeRequest request) {
        if (user == null || request == null) {
            return;
        }

        try {
            if (request.getIsActive() != null) {
                user.setIsActive(convertBooleanToInteger(request.getIsActive()));
            }
            // reason 필드는 로그용으로만 사용하고 User 엔티티에는 저장하지 않음

        } catch (Exception e) {
            System.err.println("사용자 상태 변경 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * SearchCondition의 Boolean을 Integer로 변환하여 검색 조건 생성 ✅
     */
    public Integer convertSearchActiveCondition(Boolean isActive) {
        return convertBooleanToInteger(isActive);
    }

    /**
     * 페이징된 사용자 목록을 PagedResponse로 변환 ✅
     */
    public UserInfo.PagedResponse convertToPagedResponse(
            org.springframework.data.domain.Page<User> userPage) {

        if (userPage == null) {
            return null;
        }

        try {
            java.util.List<UserInfo.ListResponse> userList = userPage.getContent().stream()
                    .map(this::convertToListResponse)
                    .filter(java.util.Objects::nonNull)
                    .collect(java.util.stream.Collectors.toList());

            return new UserInfo.PagedResponse(
                    userList,
                    userPage.getNumber(),
                    userPage.getTotalPages(),
                    userPage.getTotalElements(),
                    userPage.getSize(),
                    userPage.hasNext(),
                    userPage.hasPrevious()
            );

        } catch (Exception e) {
            System.err.println("PagedResponse 변환 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
