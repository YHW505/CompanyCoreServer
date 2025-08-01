package com.example.companycoreserver.service;

import com.example.companycoreserver.dto.LoginResponse;
import com.example.companycoreserver.entity.Enum.Role;
import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.entity.Enum.UserStatus;
import com.example.companycoreserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 모든 사용자 조회
    public List<User> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            System.out.println("Found " + users.size() + " users");
            return users;
        } catch (Exception e) {
            System.err.println("Error fetching all users: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // ID로 사용자 조회
    public Optional<User> getUserById(Long userId) {
        try {
            return userRepository.findById(userId);
        } catch (Exception e) {
            System.err.println("Error fetching user by id: " + e.getMessage());
            throw new RuntimeException("Failed to fetch user", e);
        }
    }

    // 이메일로 사용자 조회
    public Optional<User> getUserByEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            System.err.println("Error fetching user by email: " + e.getMessage());
            throw new RuntimeException("Failed to fetch user", e);
        }
    }

    // 사원번호로 사용자 조회 (메서드명 수정)
    public Optional<User> getUserByEmployeeId(String employeeCode) {
        try {
            return userRepository.findByEmployeeCode(employeeCode);
        } catch (Exception e) {
            System.err.println("Error fetching user by employee code: " + e.getMessage());
            throw new RuntimeException("Failed to fetch user", e);
        }
    }

    // 역할별 사용자 조회
    public List<User> getUsersByRole(Role role) {
        try {
            List<User> users = userRepository.findByRole(role);
            System.out.println("Found " + users.size() + " users with role: " + role);
            return users;
        } catch (Exception e) {
            System.err.println("Error fetching users by role: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // 상태별 사용자 조회
    public List<User> getUsersByStatus(UserStatus status) {
        try {
            List<User> users = userRepository.findByActiveStatus(status.equals(UserStatus.ACTIVE) ? 1 : 0);
            System.out.println("Found " + users.size() + " users with status: " + status);
            return users;
        } catch (Exception e) {
            System.err.println("Error fetching users by status: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // 부서별 사용자 조회 (부서명으로 조회)
    public List<User> getUsersByDepartment(String departmentName) {
        try {
            List<User> users = userRepository.findByDepartment(departmentName);
            System.out.println("Found " + users.size() + " users in department: " + departmentName);
            return users;
        } catch (Exception e) {
            System.err.println("Error fetching users by department: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // 부서ID별 사용자 조회 (추가 메서드)
    public List<User> getUsersByDepartmentId(Integer departmentId) {
        try {
            List<User> users = userRepository.findByDepartmentId(departmentId);
            System.out.println("Found " + users.size() + " users in department ID: " + departmentId);
            return users;
        } catch (Exception e) {
            System.err.println("Error fetching users by department ID: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // 직급별 사용자 조회 (직급명으로 조회)
    public List<User> getUsersByPosition(String positionName) {
        try {
            List<User> users = userRepository.findByPosition(positionName);
            System.out.println("Found " + users.size() + " users with position: " + positionName);
            return users;
        } catch (Exception e) {
            System.err.println("Error fetching users by position: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // 직급ID별 사용자 조회 (추가 메서드)
    public List<User> getUsersByPositionId(Integer positionId) {
        try {
            List<User> users = userRepository.findByPositionId(positionId);
            System.out.println("Found " + users.size() + " users with position ID: " + positionId);
            return users;
        } catch (Exception e) {
            System.err.println("Error fetching users by position ID: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // 이름으로 검색
    public List<User> searchUsersByName(String name) {
        try {
            List<User> users = userRepository.findByNameContaining(name);
            System.out.println("Found " + users.size() + " users with name containing: " + name);
            return users;
        } catch (Exception e) {
            System.err.println("Error searching users by name: " + e.getMessage());
            throw new RuntimeException("Failed to search users", e);
        }
    }

    // 부서와 직급으로 조회 (부서명, 직급명 사용)
    public List<User> getUsersByDepartmentAndPosition(String departmentName, String positionName) {
        try {
            return userRepository.findByDepartmentAndPosition(departmentName, positionName);
        } catch (Exception e) {
            System.err.println("Error fetching users by department and position: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // 부서ID와 직급ID로 조회 (추가 메서드)
    public List<User> getUsersByDepartmentIdAndPositionId(Integer departmentId, Integer positionId) {
        try {
            return userRepository.findByDepartmentIdAndPositionId(departmentId, positionId);
        } catch (Exception e) {
            System.err.println("Error fetching users by department ID and position ID: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // 입사일 범위로 조회
    public List<User> getUsersByHireDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return userRepository.findByHireDateRange(startDate, endDate);
        } catch (Exception e) {
            System.err.println("Error fetching users by hire date range: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // 활성 사용자만 조회
    public List<User> getActiveUsers() {
        try {
            List<User> users = userRepository.findActiveUsers();
            System.out.println("Found " + users.size() + " active users");
            return users;
        } catch (Exception e) {
            System.err.println("Error fetching active users: " + e.getMessage());
            throw new RuntimeException("Failed to fetch active users", e);
        }
    }

    // 부서별 활성 사용자 조회 (부서명 사용)
    public List<User> getActiveUsersByDepartment(String departmentName) {
        try {
            return userRepository.findActiveUsersByDepartment(departmentName);
        } catch (Exception e) {
            System.err.println("Error fetching active users by department: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // 복합 조건 검색 (ID 기반)
    public List<User> getUsersByMultipleConditions(Integer departmentId, Integer positionId,
                                                   Role role, UserStatus status) {
        try {
            Integer isActive = status != null ? (status.equals(UserStatus.ACTIVE) ? 1 : 0) : null;
            return userRepository.findByMultipleConditions(departmentId, positionId, role, isActive);
        } catch (Exception e) {
            System.err.println("Error fetching users by multiple conditions: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // 복합 조건 검색 (이름 기반) - 오버로드
    public List<User> getUsersByMultipleConditions(String departmentName, String positionName,
                                                   Role role, UserStatus status) {
        try {
            // 부서명과 직급명을 ID로 변환하는 로직이 필요한 경우
            // 현재는 기본적으로 ID 기반 검색을 사용
            Integer isActive = status != null ? (status.equals(UserStatus.ACTIVE) ? 1 : 0) : null;
            return userRepository.findByMultipleConditions(null, null, role, isActive);
        } catch (Exception e) {
            System.err.println("Error fetching users by multiple conditions: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    // 이메일 중복 체크
    public boolean isEmailExists(String email) {
        try {
            return userRepository.existsByEmail(email);
        } catch (Exception e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            throw new RuntimeException("Failed to check email", e);
        }
    }

    // 사원번호 중복 체크
    public boolean isEmployeeIdExists(String employeeCode) {
        try {
            return userRepository.existsByEmployeeCode(employeeCode);
        } catch (Exception e) {
            System.err.println("Error checking employee code existence: " + e.getMessage());
            throw new RuntimeException("Failed to check employee code", e);
        }
    }

    public LoginResponse getUserInfo(long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return null;
            }

            // User 엔티티를 LoginResponse로 변환
            LoginResponse response = new LoginResponse();
            response.setUserId(user.getUserId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setIsFirstLogin(user.getIsFirstLogin());

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return false;
            }

            User user = userOptional.get();

            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                System.out.println("Current password mismatch for user: " + userId);
                return false;
            }

            // 새 비밀번호 암호화 후 저장
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedNewPassword);
            userRepository.save(user);

            System.out.println("Password changed successfully for user: " + userId);
            return true;

        } catch (Exception e) {
            System.err.println("Error changing password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
