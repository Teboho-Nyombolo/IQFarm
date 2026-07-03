package com.example.server.repository;

import com.example.server.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropRepository extends JpaRepository <Crop, Long>{

    List<Crop> findByFarm_FarmId(Long farmId);

    List<Crop> findByCropType(String cropType);

    List<Crop> findByStatus(String status);

}
