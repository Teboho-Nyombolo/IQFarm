package com.example.server.repository;

import com.example.server.dto.request.FarmRequestDTO;
import com.example.server.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Long> {

    //public List<Farm> getFarms();

    Optional<Farm> findById(Long ownerId);
}
