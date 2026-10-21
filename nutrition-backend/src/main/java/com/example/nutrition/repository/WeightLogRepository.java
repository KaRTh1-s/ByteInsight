package com.example.nutrition.repository;

import com.example.nutrition.entity.WeightLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {
    List<WeightLog> findByUserIdOrderByLogDateAsc(Long userId);
    List<WeightLog> findByUserUsernameOrderByLogDateAsc(String username);

    @Query("SELECT w FROM WeightLog w WHERE w.user.username = :username AND w.logDate BETWEEN :startDate AND :endDate ORDER BY w.logDate ASC")
    List<WeightLog> findByUsernameAndDateRange(@Param("username") String username, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
