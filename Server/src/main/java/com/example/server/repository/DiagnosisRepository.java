package com.example.server.repository;

import com.example.server.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    Optional<Diagnosis> findByImage_ImageId(Long imageId);

    List<Diagnosis> findByCropTracker_TrackerId(Long trackerId);

    List<Diagnosis> findByCrop_CropId(Long cropId);
}