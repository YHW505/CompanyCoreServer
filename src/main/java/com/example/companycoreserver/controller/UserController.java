// controller/UserController.java
package com.example.companycoreserver.controller;

import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.entity.Enum.Role;
import com.example.companycoreserver.entity.Enum.UserStatus;
import com.example.companycoreserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // JWT 토큰 검증 (비활성화 상태)
    private boolean isValidToken(String token) {
        return true; // JWT 비활성화 상태이므로 항상 true
        // return token != null && !token.trim().isEmpty();
    }

    // 🆕 사용자 생성
    @PostMapping("/create")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            System.out.println("=== /users/create 요청 받음 ===");
            System.out.println("받은 사용자 데이터: " + user);

            // 필수 필드 검증
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("이메일은 필수입니다.");
            }
            if (user.getEmployeeCode() == null || user.getEmployeeCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("사원번호는 필수입니다.");
            }
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("이름은 필수입니다.");
            }

            // 이메일 중복 체크
            if (userService.isEmailExists(user.getEmail())) {
                return ResponseEntity.badRequest()
                        .body("이미 존재하는 이메일입니다: " + user.getEmail());
            }

            // 사원번호 중복 체크
            if (userService.isEmployeeIdExists(user.getEmployeeCode())) {
                return ResponseEntity.badRequest()
                        .body("이미 존재하는 사원번호입니다: " + user.getEmployeeCode());
            }

            User createdUser = userService.createUser(user);
            System.out.println("사용자 생성 성공: " + createdUser.getUserId());
            return ResponseEntity.ok(createdUser);

        } catch (Exception e) {
            System.err.println("Error in registerUser: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("사용자 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 🔍 모든 사용자 조회
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(
            @RequestHeader("Authorization") String token) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            System.err.println("Error in getAllUsers: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 ID로 사용자 조회
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Optional<User> user = userService.getUserById(userId);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error in getUserById: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    // 🆕 사용자 부서 정보 디버깅용
    @GetMapping("/debug/department-info")
    public ResponseEntity<String> getDepartmentInfo(
            @RequestHeader("Authorization") String token) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<User> allUsers = userService.getAllUsers();
            StringBuilder result = new StringBuilder();
            result.append("=== 사용자 부서 정보 ===\n");
            
            for (User user : allUsers) {
                String departmentInfo = user.getDepartment() != null ? 
                    user.getDepartment().getDepartmentName() : "null";
                result.append(String.format("사용자: %s (ID: %d), 부서: %s\n", 
                    user.getUsername(), user.getUserId(), departmentInfo));
            }
            
            return ResponseEntity.ok(result.toString());

        } catch (Exception e) {
            System.err.println("Error in getDepartmentInfo: " + e.getMessage());
            return ResponseEntity.status(500).body("오류: " + e.getMessage());
        }
    }

    // 🔍 이메일로 사용자 조회
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(
//            @RequestHeader("Authorization") String token,
            @PathVariable String email) {
        try {
//            if (!isValidToken(token)) {
//                return ResponseEntity.status(401).build();
//            }

            Optional<User> user = userService.getUserByEmail(email);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error in getUserByEmail: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 사원번호로 사용자 조회
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<User> getUserByEmployeeId(
            @RequestHeader("Authorization") String token,
            @PathVariable String employeeId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Optional<User> user = userService.getUserByEmployeeId(employeeId);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error in getUserByEmployeeId: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 역할별 사용자 조회
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(
            @RequestHeader("Authorization") String token,
            @PathVariable Role role) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<User> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            System.err.println("Error in getUsersByRole: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 상태별 사용자 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable UserStatus status) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<User> users = userService.getUsersByStatus(status);
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            System.err.println("Error in getUsersByStatus: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 부서별 사용자 조회
    @GetMapping("/department/{department}")
    public ResponseEntity<List<User>> getUsersByDepartment(
            @RequestHeader("Authorization") String token,
            @PathVariable String department) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<User> users = userService.getUsersByDepartment(department);
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            System.err.println("Error in getUsersByDepartment: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 직급별 사용자 조회
    @GetMapping("/position/{position}")
    public ResponseEntity<List<User>> getUsersByPosition(
            @RequestHeader("Authorization") String token,
            @PathVariable String position) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<User> users = userService.getUsersByPosition(position);
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            System.err.println("Error in getUsersByPosition: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 이름으로 검색
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(
            @RequestHeader("Authorization") String token,
            @RequestParam String name) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<User> users = userService.searchUsersByName(name);
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            System.err.println("Error in searchUsersByName: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 부서와 직급으로 조회
    @GetMapping("/department/{department}/position/{position}")
    public ResponseEntity<List<User>> getUsersByDepartmentAndPosition(
            @RequestHeader("Authorization") String token,
            @PathVariable String department,
            @PathVariable String position) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<User> users = userService.getUsersByDepartmentAndPosition(department, position);
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            System.err.println("Error in getUsersByDepartmentAndPosition: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 입사일 범위로 조회
    @GetMapping("/hire-date-range")
    public ResponseEntity<List<User>> getUsersByHireDateRange(
            @RequestHeader("Authorization") String token,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<User> users = userService.getUsersByHireDateRange(startDate, endDate);
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            System.err.println("Error in getUsersByHireDateRange: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 활성 사용자만 조회
    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers(
            @RequestHeader("Authorization") String token) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<User> users = userService.getActiveUsers();
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            System.err.println("Error in getActiveUsers: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 부서별 활성 사용자 조회
    @GetMapping("/active/department/{department}")
    public ResponseEntity<List<User>> getActiveUsersByDepartment(
            @RequestHeader("Authorization") String token,
            @PathVariable String department) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<User> users = userService.getActiveUsersByDepartment(department);
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            System.err.println("Error in getActiveUsersByDepartment: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 복합 조건 검색
    @GetMapping("/filter")
    public ResponseEntity<List<User>> getUsersByMultipleConditions(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) UserStatus status) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<User> users = userService.getUsersByMultipleConditions(department, position, role, status);
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            System.err.println("Error in getUsersByMultipleConditions: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 이메일 중복 체크
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(
            @RequestHeader("Authorization") String token,
            @PathVariable String email) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            boolean exists = userService.isEmailExists(email);
            return ResponseEntity.ok(exists);

        } catch (Exception e) {
            System.err.println("Error in checkEmailExists: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 사원번호 중복 체크
    @GetMapping("/check-employee/{employeeId}")
    public ResponseEntity<Boolean> checkEmployeeIdExists(
            @RequestHeader("Authorization") String token,
            @PathVariable String employeeId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            boolean exists = userService.isEmployeeIdExists(employeeId);
            return ResponseEntity.ok(exists);

        } catch (Exception e) {
            System.err.println("Error in checkEmployeeIdExists: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
