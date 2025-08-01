package com.example.companycoreserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInfo {

    /**
     * 사용자 생성 요청 DTO
     */
    public static class CreateRequest {
        private String employeeCode;
        private String username;
        private String email;
        private String password;
        private String phone;
        private String birthDate;
        private String role;
        private Long departmentId;
        private Long positionId;

        // 기본 생성자
        public CreateRequest() {
        }

        // 전체 생성자
        public CreateRequest(String employeeCode, String username, String email, String password,
                             String phone, String birthDate, String role, Long departmentId, Long positionId) {
            this.employeeCode = employeeCode;
            this.username = username;
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.birthDate = birthDate;
            this.role = role;
            this.departmentId = departmentId;
            this.positionId = positionId;
        }

        // Getters
        public String getEmployeeCode() {
            return employeeCode;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getPhone() {
            return phone;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public String getRole() {
            return role;
        }

        public Long getDepartmentId() {
            return departmentId;
        }

        public Long getPositionId() {
            return positionId;
        }

        // Setters
        public void setEmployeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setDepartmentId(Long departmentId) {
            this.departmentId = departmentId;
        }

        public void setPositionId(Long positionId) {
            this.positionId = positionId;
        }
    }

    /**
     * 사용자 정보 수정 요청 DTO
     */
    public static class UpdateRequest {
        private String username;
        private String email;
        private String phone;
        private String birthDate;
        private Long departmentId;
        private Long positionId;

        // 기본 생성자
        public UpdateRequest() {
        }

        // 전체 생성자
        public UpdateRequest(String username, String email, String phone, String birthDate,
                             Long departmentId, Long positionId) {
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.birthDate = birthDate;
            this.departmentId = departmentId;
            this.positionId = positionId;
        }

        // Getters
        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public Long getDepartmentId() {
            return departmentId;
        }

        public Long getPositionId() {
            return positionId;
        }

        // Setters
        public void setUsername(String username) {
            this.username = username;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public void setDepartmentId(Long departmentId) {
            this.departmentId = departmentId;
        }

        public void setPositionId(Long positionId) {
            this.positionId = positionId;
        }
    }

    /**
     * 비밀번호 변경 요청 DTO
     */
    public static class PasswordChangeRequest {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;

        // 기본 생성자
        public PasswordChangeRequest() {
        }

        // 전체 생성자
        public PasswordChangeRequest(String currentPassword, String newPassword, String confirmPassword) {
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
            this.confirmPassword = confirmPassword;
        }

        // Getters
        public String getCurrentPassword() {
            return currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        // Setters
        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }

    /**
     * 로그인 요청 DTO
     */
    public static class LoginRequest {
        private String email;
        private String password;

        // 기본 생성자
        public LoginRequest() {
        }

        // 전체 생성자
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        // Getters
        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        // Setters
        public void setEmail(String email) {
            this.email = email;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 사용자 상세 정보 응답 DTO (기존 UserInfo 기반)
     */
    public static class Response {
        private Long userId;
        private String employeeCode;
        private String username;
        private String email;
        private String phone;
        private String birthDate;
        private String role;
        private String departmentName;
        private String positionName;

        @JsonProperty("isFirstLogin")
        private Integer isFirstLogin;

        @JsonProperty("isActive")
        private Integer isActive;

        private String createdAt;
        private String updatedAt;

        // 기본 생성자
        public Response() {
        }

        // 전체 생성자
        public Response(Long userId, String employeeCode, String username, String email,
                        String phone, String birthDate, String role, String departmentName,
                        String positionName, Integer isFirstLogin, Integer isActive,
                        String createdAt, String updatedAt) {
            this.userId = userId;
            this.employeeCode = employeeCode;
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.birthDate = birthDate;
            this.role = role;
            this.departmentName = departmentName;
            this.positionName = positionName;
            this.isFirstLogin = isFirstLogin;
            this.isActive = isActive;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        // Getters
        public Long getUserId() {
            return userId;
        }

        public String getEmployeeCode() {
            return employeeCode;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public String getRole() {
            return role;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public String getPositionName() {
            return positionName;
        }

        public Integer getIsFirstLogin() {
            return isFirstLogin;
        }

        public Integer getIsActive() {
            return isActive;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        // Setters
        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public void setEmployeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public void setPositionName(String positionName) {
            this.positionName = positionName;
        }

        public void setIsFirstLogin(Integer isFirstLogin) {
            this.isFirstLogin = isFirstLogin;
        }

        public void setIsActive(Integer isActive) {
            this.isActive = isActive;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        // 편의 메서드 (기존 UserInfo의 메서드 유지)
        public boolean isFirstLoginBoolean() {
            return isFirstLogin != null && isFirstLogin == 1;
        }

        public boolean isActiveBoolean() {
            return isActive != null && isActive == 1;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "userId=" + userId +
                    ", employeeCode='" + employeeCode + '\'' +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", birthDate='" + birthDate + '\'' +
                    ", role='" + role + '\'' +
                    ", departmentName='" + departmentName + '\'' +
                    ", positionName='" + positionName + '\'' +
                    ", isFirstLogin=" + isFirstLogin +
                    ", isActive=" + isActive +
                    ", createdAt='" + createdAt + '\'' +
                    ", updatedAt='" + updatedAt + '\'' +
                    '}';
        }
    }

    /**
     * 사용자 목록 조회 응답 DTO (간소화된 정보)
     */
    public static class ListResponse {
        private Long userId;
        private String employeeCode;
        private String username;
        private String email;
        private String departmentName;
        private String positionName;

        @JsonProperty("isActive")
        private Integer isActive;

        private String createdAt;

        // 기본 생성자
        public ListResponse() {
        }

        // 전체 생성자
        public ListResponse(Long userId, String employeeCode, String username, String email,
                            String departmentName, String positionName, Integer isActive, String createdAt) {
            this.userId = userId;
            this.employeeCode = employeeCode;
            this.username = username;
            this.email = email;
            this.departmentName = departmentName;
            this.positionName = positionName;
            this.isActive = isActive;
            this.createdAt = createdAt;
        }

        // Getters
        public Long getUserId() {
            return userId;
        }

        public String getEmployeeCode() {
            return employeeCode;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public String getPositionName() {
            return positionName;
        }

        public Integer getIsActive() {
            return isActive;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        // Setters
        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public void setEmployeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public void setPositionName(String positionName) {
            this.positionName = positionName;
        }

        public void setIsActive(Integer isActive) {
            this.isActive = isActive;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        // 편의 메서드
        public boolean isActiveBoolean() {
            return isActive != null && isActive == 1;
        }
    }

    /**
     * 로그인 성공 응답 DTO
     */
    public static class LoginResponse {
        private Long userId;
        private String employeeCode;
        private String username;
        private String email;
        private String role;

        @JsonProperty("isFirstLogin")
        private Integer isFirstLogin;

        private String accessToken;
        private String refreshToken;
        private Long tokenExpiresIn;

        // 기본 생성자
        public LoginResponse() {
        }

        // 전체 생성자
        public LoginResponse(Long userId, String employeeCode, String username, String email,
                             String role, Integer isFirstLogin, String accessToken, String refreshToken, Long tokenExpiresIn) {
            this.userId = userId;
            this.employeeCode = employeeCode;
            this.username = username;
            this.email = email;
            this.role = role;
            this.isFirstLogin = isFirstLogin;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.tokenExpiresIn = tokenExpiresIn;
        }

        // Getters
        public Long getUserId() {
            return userId;
        }

        public String getEmployeeCode() {
            return employeeCode;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }

        public Integer getIsFirstLogin() {
            return isFirstLogin;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public Long getTokenExpiresIn() {
            return tokenExpiresIn;
        }

        // Setters
        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public void setEmployeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setIsFirstLogin(Integer isFirstLogin) {
            this.isFirstLogin = isFirstLogin;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public void setTokenExpiresIn(Long tokenExpiresIn) {
            this.tokenExpiresIn = tokenExpiresIn;
        }

        // 편의 메서드
        public boolean isFirstLoginBoolean() {
            return isFirstLogin != null && isFirstLogin == 1;
        }
    }

    /**
     * 사용자 검색 조건 DTO
     */
    public static class SearchCondition {
        private String keyword;
        private Long departmentId;
        private String role;
        private Integer isActive;
        private String startDate;
        private String endDate;
        private Integer page = 0;
        private Integer size = 10;
        private String sortBy = "createdAt";
        private String sortDirection = "DESC";

        // 기본 생성자
        public SearchCondition() {
        }

        // 전체 생성자
        public SearchCondition(String keyword, Long departmentId, String role, Integer isActive,
                               String startDate, String endDate, Integer page, Integer size,
                               String sortBy, String sortDirection) {
            this.keyword = keyword;
            this.departmentId = departmentId;
            this.role = role;
            this.isActive = isActive;
            this.startDate = startDate;
            this.endDate = endDate;
            this.page = page;
            this.size = size;
            this.sortBy = sortBy;
            this.sortDirection = sortDirection;
        }

        // Getters
        public String getKeyword() {
            return keyword;
        }

        public Long getDepartmentId() {
            return departmentId;
        }

        public String getRole() {
            return role;
        }

        public Integer getIsActive() {
            return isActive;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public Integer getPage() {
            return page;
        }

        public Integer getSize() {
            return size;
        }

        public String getSortBy() {
            return sortBy;
        }

        public String getSortDirection() {
            return sortDirection;
        }

        // Setters
        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public void setDepartmentId(Long departmentId) {
            this.departmentId = departmentId;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setIsActive(Integer isActive) {
            this.isActive = isActive;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public void setSortBy(String sortBy) {
            this.sortBy = sortBy;
        }

        public void setSortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
        }
    }

    /**
     * 사용자 상태 변경 요청 DTO
     */
    public static class StatusChangeRequest {
        private Integer isActive; // 0: 비활성화, 1: 활성화
        private String reason; // 상태 변경 사유

        // 기본 생성자
        public StatusChangeRequest() {
        }

        // 전체 생성자
        public StatusChangeRequest(Integer isActive, String reason) {
            this.isActive = isActive;
            this.reason = reason;
        }

        // Getters
        public Integer getIsActive() {
            return isActive;
        }

        public String getReason() {
            return reason;
        }

        // Setters
        public void setIsActive(Integer isActive) {
            this.isActive = isActive;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    /**
     * 페이징된 사용자 목록 응답 DTO
     */
    public static class PagedResponse {
        private java.util.List<ListResponse> users;
        private Integer currentPage;
        private Integer totalPages;
        private Long totalElements;
        private Integer size;
        private boolean hasNext;
        private boolean hasPrevious;

        // 기본 생성자
        public PagedResponse() {
        }

        // 전체 생성자
        public PagedResponse(java.util.List<ListResponse> users, Integer currentPage, Integer totalPages,
                             Long totalElements, Integer size, boolean hasNext, boolean hasPrevious) {
            this.users = users;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.size = size;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }

        // Getters
        public java.util.List<ListResponse> getUsers() {
            return users;
        }

        public Integer getCurrentPage() {
            return currentPage;
        }

        public Integer getTotalPages() {
            return totalPages;
        }

        public Long getTotalElements() {
            return totalElements;
        }

        public Integer getSize() {
            return size;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public boolean isHasPrevious() {
            return hasPrevious;
        }

        // Setters
        public void setUsers(java.util.List<ListResponse> users) {
            this.users = users;
        }

        public void setCurrentPage(Integer currentPage) {
            this.currentPage = currentPage;
        }

        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }

        public void setTotalElements(Long totalElements) {
            this.totalElements = totalElements;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }
    }
}
