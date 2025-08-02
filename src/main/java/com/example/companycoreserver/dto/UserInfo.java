package com.example.companycoreserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.LocalDate;

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
        private LocalDate birthDate; // String -> LocalDate
        private String role;
        private Long departmentId;
        private Long positionId;
        private LocalDate joinDate; // 추가

        // 기본 생성자
        public CreateRequest() {
        }

        // 전체 생성자
        public CreateRequest(String employeeCode, String username, String email, String password,
                             String phone, LocalDate birthDate, String role, Long departmentId,
                             Long positionId, LocalDate joinDate) {
            this.employeeCode = employeeCode;
            this.username = username;
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.birthDate = birthDate;
            this.role = role;
            this.departmentId = departmentId;
            this.positionId = positionId;
            this.joinDate = joinDate;
        }

        // Getters & Setters
        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public LocalDate getBirthDate() { return birthDate; }
        public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

        public Long getPositionId() { return positionId; }
        public void setPositionId(Long positionId) { this.positionId = positionId; }

        public LocalDate getJoinDate() { return joinDate; }
        public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
    }

    /**
     * 사용자 정보 수정 요청 DTO
     */
    public static class UpdateRequest {
        private String username;
        private String email;
        private String phone;
        private LocalDate birthDate; // String -> LocalDate
        private Long departmentId;
        private Long positionId;

        // 기본 생성자
        public UpdateRequest() {
        }

        // 전체 생성자
        public UpdateRequest(String username, String email, String phone, LocalDate birthDate,
                             Long departmentId, Long positionId) {
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.birthDate = birthDate;
            this.departmentId = departmentId;
            this.positionId = positionId;
        }

        // Getters & Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public LocalDate getBirthDate() { return birthDate; }
        public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

        public Long getPositionId() { return positionId; }
        public void setPositionId(Long positionId) { this.positionId = positionId; }
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

        // Getters & Setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
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

        // Getters & Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    /**
     * 사용자 상세 정보 응답 DTO (DB 테이블 구조에 맞게 수정)
     */
    public static class Response {
        private Long userId;
        private String employeeCode;
        private String username;
        private LocalDate joinDate; // 추가
        private String email;
        private String phone;
        private LocalDate birthDate; // String -> LocalDate
        private String role;
        private Long positionId; // 추가
        private Long departmentId; // 추가
        private String departmentName;
        private String positionName;

        @JsonProperty("isFirstLogin")
        private Boolean isFirstLogin; // Integer -> Boolean

        @JsonProperty("isActive")
        private Boolean isActive; // Integer -> Boolean

        private LocalDateTime createdAt; // String -> LocalDateTime

        // 기본 생성자
        public Response() {
        }

        // 전체 생성자
        public Response(Long userId, String employeeCode, String username, LocalDate joinDate,
                        String email, String phone, LocalDate birthDate, String role,
                        Long positionId, Long departmentId, String departmentName, String positionName,
                        Boolean isFirstLogin, Boolean isActive, LocalDateTime createdAt) {
            this.userId = userId;
            this.employeeCode = employeeCode;
            this.username = username;
            this.joinDate = joinDate;
            this.email = email;
            this.phone = phone;
            this.birthDate = birthDate;
            this.role = role;
            this.positionId = positionId;
            this.departmentId = departmentId;
            this.departmentName = departmentName;
            this.positionName = positionName;
            this.isFirstLogin = isFirstLogin;
            this.isActive = isActive;
            this.createdAt = createdAt;
        }

        // Getters & Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public LocalDate getJoinDate() { return joinDate; }
        public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public LocalDate getBirthDate() { return birthDate; }
        public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public Long getPositionId() { return positionId; }
        public void setPositionId(Long positionId) { this.positionId = positionId; }

        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

        public String getPositionName() { return positionName; }
        public void setPositionName(String positionName) { this.positionName = positionName; }

        public Boolean getIsFirstLogin() { return isFirstLogin; }
        public void setIsFirstLogin(Boolean isFirstLogin) { this.isFirstLogin = isFirstLogin; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        // 편의 메서드
        public boolean isFirstLoginBoolean() {
            return isFirstLogin != null && isFirstLogin;
        }

        public boolean isActiveBoolean() {
            return isActive != null && isActive;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "userId=" + userId +
                    ", employeeCode='" + employeeCode + '\'' +
                    ", username='" + username + '\'' +
                    ", joinDate=" + joinDate +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", birthDate=" + birthDate +
                    ", role='" + role + '\'' +
                    ", positionId=" + positionId +
                    ", departmentId=" + departmentId +
                    ", departmentName='" + departmentName + '\'' +
                    ", positionName='" + positionName + '\'' +
                    ", isFirstLogin=" + isFirstLogin +
                    ", isActive=" + isActive +
                    ", createdAt=" + createdAt +
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
        private LocalDate joinDate; // 추가
        private String email;
        private String departmentName;
        private String positionName;

        @JsonProperty("isActive")
        private Boolean isActive; // Integer -> Boolean

        private LocalDateTime createdAt; // String -> LocalDateTime

        // 기본 생성자
        public ListResponse() {
        }

        // 전체 생성자
        public ListResponse(Long userId, String employeeCode, String username, LocalDate joinDate,
                            String email, String departmentName, String positionName,
                            Boolean isActive, LocalDateTime createdAt) {
            this.userId = userId;
            this.employeeCode = employeeCode;
            this.username = username;
            this.joinDate = joinDate;
            this.email = email;
            this.departmentName = departmentName;
            this.positionName = positionName;
            this.isActive = isActive;
            this.createdAt = createdAt;
        }

        // Getters & Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public LocalDate getJoinDate() { return joinDate; }
        public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

        public String getPositionName() { return positionName; }
        public void setPositionName(String positionName) { this.positionName = positionName; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        // 편의 메서드
        public boolean isActiveBoolean() {
            return isActive != null && isActive;
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
        private Boolean isFirstLogin; // Integer -> Boolean

        private String accessToken;
        private String refreshToken;
        private Long tokenExpiresIn;

        // 기본 생성자
        public LoginResponse() {
        }

        // 전체 생성자
        public LoginResponse(Long userId, String employeeCode, String username, String email,
                             String role, Boolean isFirstLogin, String accessToken,
                             String refreshToken, Long tokenExpiresIn) {
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

        // Getters & Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public Boolean getIsFirstLogin() { return isFirstLogin; }
        public void setIsFirstLogin(Boolean isFirstLogin) { this.isFirstLogin = isFirstLogin; }

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

        public Long getTokenExpiresIn() { return tokenExpiresIn; }
        public void setTokenExpiresIn(Long tokenExpiresIn) { this.tokenExpiresIn = tokenExpiresIn; }

        // 편의 메서드
        public boolean isFirstLoginBoolean() {
            return isFirstLogin != null && isFirstLogin;
        }
    }

    /**
     * 사용자 검색 조건 DTO
     */
    public static class SearchCondition {
        private String keyword;
        private Long departmentId;
        private String role;
        private Boolean isActive; // Integer -> Boolean
        private LocalDate startDate; // String -> LocalDate
        private LocalDate endDate; // String -> LocalDate
        private Integer page = 0;
        private Integer size = 10;
        private String sortBy = "createdAt";
        private String sortDirection = "DESC";

        // 기본 생성자
        public SearchCondition() {
        }

        // 전체 생성자
        public SearchCondition(String keyword, Long departmentId, String role, Boolean isActive,
                               LocalDate startDate, LocalDate endDate, Integer page, Integer size,
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

        // Getters & Setters
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }

        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }

        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }

        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }

        public String getSortDirection() { return sortDirection; }
        public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
    }

    /**
     * 사용자 상태 변경 요청 DTO
     */
    public static class StatusChangeRequest {
        private Boolean isActive; // Integer -> Boolean
        private String reason; // 상태 변경 사유

        // 기본 생성자
        public StatusChangeRequest() {
        }

        // 전체 생성자
        public StatusChangeRequest(Boolean isActive, String reason) {
            this.isActive = isActive;
            this.reason = reason;
        }

        // Getters & Setters
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
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

        // Getters & Setters
        public java.util.List<ListResponse> getUsers() { return users; }
        public void setUsers(java.util.List<ListResponse> users) { this.users = users; }

        public Integer getCurrentPage() { return currentPage; }
        public void setCurrentPage(Integer currentPage) { this.currentPage = currentPage; }

        public Integer getTotalPages() { return totalPages; }
        public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }

        public Long getTotalElements() { return totalElements; }
        public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }

        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }

        public boolean isHasNext() { return hasNext; }
        public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }

        public boolean isHasPrevious() { return hasPrevious; }
        public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }
    }
}
