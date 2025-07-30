package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmployeeCode(String employeeCode);
    boolean existsByEmployeeCode(String employeeCode);
}