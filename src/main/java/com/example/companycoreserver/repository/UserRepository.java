package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.Enum.Role;
import com.example.companycoreserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회 (로그인용)
    Optional<User> findByEmail(String email);

    // 사원번호로 사용자 조회
    Optional<User> findByEmployeeCode(String employeeCode);

    // 역할별 사용자 조회
    List<User> findByRole(Role role);

    // 상태별 사용자 조회 (isActive 기준 - Integer 타입)
    @Query("SELECT u FROM User u WHERE u.isActive = :isActive")
    List<User> findByActiveStatus(@Param("isActive") Integer isActive);

    // 부서ID별 사용자 조회
    List<User> findByDepartmentId(Integer departmentId);

    // 직급ID별 사용자 조회
    List<User> findByPositionId(Integer positionId);

    // 사용자명으로 검색 (부분 일치)
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    List<User> findByUsernameContaining(@Param("username") String username);

    // Service에서 사용하는 메서드명과 일치하도록 추가
    @Query("SELECT u FROM User u WHERE u.username LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);

    // 부서ID와 직급ID로 조회
    List<User> findByDepartmentIdAndPositionId(Integer departmentId, Integer positionId);

    // 부서ID와 활성 상태로 조회
    @Query("SELECT u FROM User u WHERE u.departmentId = :departmentId AND u.isActive = :isActive")
    List<User> findByDepartmentIdAndActiveStatus(@Param("departmentId") Integer departmentId,
                                                 @Param("isActive") Integer isActive);

    // 입사일 범위로 조회 (Service에서 사용하는 메서드명과 일치)
    @Query("SELECT u FROM User u WHERE u.joinDate >= :startDate AND u.joinDate <= :endDate")
    List<User> findByHireDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 활성 사용자만 조회
    @Query("SELECT u FROM User u WHERE u.isActive = 1")
    List<User> findActiveUsers();

    // 비활성 사용자만 조회
    @Query("SELECT u FROM User u WHERE u.isActive = 0")
    List<User> findInactiveUsers();

    // 부서별 활성 사용자 조회 (Service 호환을 위한 부서명 버전)
    @Query("SELECT u FROM User u JOIN u.department d WHERE d.departmentName = :departmentName AND u.isActive = 1")
    List<User> findActiveUsersByDepartment(@Param("departmentName") String departmentName);

    // 부서ID별 활성 사용자 조회
    @Query("SELECT u FROM User u WHERE u.departmentId = :departmentId AND u.isActive = 1")
    List<User> findActiveUsersByDepartmentId(@Param("departmentId") Integer departmentId);

    // 복합 조건 검색 (주석 해제 및 수정)
    @Query("SELECT u FROM User u WHERE " +
            "(:departmentId IS NULL OR u.departmentId = :departmentId) AND " +
            "(:positionId IS NULL OR u.positionId = :positionId) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:isActive IS NULL OR u.isActive = :isActive)")
    List<User> findByMultipleConditions(@Param("departmentId") Integer departmentId,
                                        @Param("positionId") Integer positionId,
                                        @Param("role") Role role,
                                        @Param("isActive") Integer isActive);

    // 이메일 중복 체크
    boolean existsByEmail(String email);

    // 사원번호 중복 체크
    boolean existsByEmployeeCode(String employeeCode);

    // 활성 상태로 이메일 찾기
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = 1")
    Optional<User> findByEmailAndActive(@Param("email") String email);

    // 활성 상태로 사원번호 찾기
    @Query("SELECT u FROM User u WHERE u.employeeCode = :employeeCode AND u.isActive = 1")
    Optional<User> findByEmployeeCodeAndActive(@Param("employeeCode") String employeeCode);

    // 부서명으로 조회 (조인 사용) - Service 호환
    @Query("SELECT u FROM User u JOIN u.department d WHERE d.departmentName = :departmentName")
    List<User> findByDepartment(@Param("departmentName") String departmentName);

    // 직급명으로 조회 (조인 사용) - Service 호환
    @Query("SELECT u FROM User u JOIN u.position p WHERE p.positionName = :positionName")
    List<User> findByPosition(@Param("positionName") String positionName);

    // 부서명과 직급명으로 조회 - Service 호환
    @Query("SELECT u FROM User u JOIN u.department d JOIN u.position p " +
            "WHERE d.departmentName = :departmentName AND p.positionName = :positionName")
    List<User> findByDepartmentAndPosition(@Param("departmentName") String departmentName,
                                           @Param("positionName") String positionName);

    // 부서명으로 조회 (별칭)
    @Query("SELECT u FROM User u JOIN u.department d WHERE d.departmentName = :departmentName")
    List<User> findByDepartmentName(@Param("departmentName") String departmentName);

    // 직급명으로 조회 (별칭)
    @Query("SELECT u FROM User u JOIN u.position p WHERE p.positionName = :positionName")
    List<User> findByPositionName(@Param("positionName") String positionName);

    // 부서명과 직급명으로 조회 (별칭)
    @Query("SELECT u FROM User u JOIN u.department d JOIN u.position p " +
            "WHERE d.departmentName = :departmentName AND p.positionName = :positionName")
    List<User> findByDepartmentNameAndPositionName(@Param("departmentName") String departmentName,
                                                   @Param("positionName") String positionName);

    // 기타 메서드들
    List<User> findByBirthDate(LocalDate birthDate);
    Optional<User> findByPhone(String phone);
    Optional<User> findByUsername(String username);

    // 첫 로그인 여부로 조회
    @Query("SELECT u FROM User u WHERE u.isFirstLogin = :isFirstLogin")
    List<User> findByFirstLoginStatus(@Param("isFirstLogin") Integer isFirstLogin);

    // 활성 상태이면서 첫 로그인인 사용자들
    @Query("SELECT u FROM User u WHERE u.isActive = 1 AND u.isFirstLogin = 1")
    List<User> findActiveFirstLoginUsers();
}
