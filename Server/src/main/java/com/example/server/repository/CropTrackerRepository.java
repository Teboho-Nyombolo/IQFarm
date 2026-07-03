package com.example.server.repository;

import com.example.server.entity.CropTracker;
import com.example.server.enums.LogType;
import com.example.server.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CropTrackerRepository extends JpaRepository<CropTracker, Long> {

    List<CropTracker> findByCrop_CropId(Long cropId);

    List<CropTracker> findByStatus(Status status);

    List<CropTracker> findByLogType(LogType logType);

    List<CropTracker> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<CropTracker> findByCompleted(Boolean completed);
}
