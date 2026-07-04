package com.example.server.repository;

import com.example.server.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository <Image, Long>{

    List<Image> findByCrop_CropId(Long cropId);

    List<Image> findByCropTracker_TrackerId(Long trackerId);

    List<Image> findByUploadedBy_UserId(Long userId);

}
