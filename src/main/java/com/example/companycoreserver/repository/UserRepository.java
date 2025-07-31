package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmployeeCode(String employeeCode);
    boolean existsByEmployeeCode(String employeeCode);
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.isActive = 1") // Integer 1로 변경
    Optional<User> findActiveUserById(@Param("userId") Long userId);
    @Query("SELECT u FROM User u WHERE u.employeeCode = :employeeCode AND u.isActive = 1")
    Optional<User> findActiveUserByEmployeeCode(@Param("employeeCode") String employeeCode);

    boolean existsByEmail(String email);
}
