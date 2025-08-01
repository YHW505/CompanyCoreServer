package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // 부서명으로 검색
    Optional<Department> findByDepartmentName(String departmentName);

    // 부서명에 특정 키워드가 포함된 부서들
    List<Department> findByDepartmentNameContainingIgnoreCase(String keyword);

    // 특정 부서 조회
    @Query("SELECT d FROM Department d WHERE d.departmentId = :departmentId")
    Optional<Department> findDepartmentById(@Param("departmentId") Long departmentId);

    // 모든 부서를 이름순으로 정렬
    List<Department> findAllByOrderByDepartmentNameAsc();

    // 모든 부서를 ID순으로 정렬
    List<Department> findAllByOrderByDepartmentIdAsc();

    // 부서별 사용자 수 통계
    @Query("SELECT d.departmentName, COUNT(u) FROM Department d " +
            "LEFT JOIN User u ON d.departmentId = u.department.departmentId " +
            "GROUP BY d.departmentId, d.departmentName")
    List<Object[]> findDepartmentUserCounts();

    // 특정 부서의 사용자 수
    @Query("SELECT COUNT(u) FROM User u WHERE u.department.departmentId = :departmentId")
    Long countUsersByDepartment(@Param("departmentId") Long departmentId);

    // 부서명 중복 체크
    boolean existsByDepartmentName(String departmentName);

    // 사용자가 있는 부서들만 조회
    @Query("SELECT DISTINCT d FROM Department d " +
            "INNER JOIN User u ON d.departmentId = u.department.departmentId")
    List<Department> findDepartmentsWithUsers();

    // 사용자가 없는 부서들 조회
    @Query("SELECT d FROM Department d " +
            "WHERE d.departmentId NOT IN (SELECT DISTINCT u.department.departmentId FROM User u WHERE u.department IS NOT NULL)")
    List<Department> findDepartmentsWithoutUsers();

    // 부서명으로 부분 검색 (대소문자 구분 없음)
    @Query("SELECT d FROM Department d WHERE LOWER(d.departmentName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Department> searchByDepartmentName(@Param("keyword") String keyword);
}
