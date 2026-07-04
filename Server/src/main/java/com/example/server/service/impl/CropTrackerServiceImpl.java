package com.example.server.service.impl;

import com.example.server.dto.request.CropTrackerRequestDTO;
import com.example.server.dto.response.CropTrackerResponseDTO;
import com.example.server.entity.Crop;
import com.example.server.entity.CropTracker;
import com.example.server.enums.LogType;
import com.example.server.enums.Status;
import com.example.server.mapper.CropTrackerMapper;
import com.example.server.repository.CropRepository;
import com.example.server.repository.CropTrackerRepository;
import com.example.server.service.CropTrackerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CropTrackerServiceImpl implements CropTrackerService {

    private final CropTrackerRepository cropTrackerRepository;
    private final CropRepository cropRepository;
    private final CropTrackerMapper cropTrackerMapper;

    public CropTrackerServiceImpl(CropTrackerRepository cropTrackerRepository, CropRepository cropRepository, CropTrackerMapper cropTrackerMapper) {
        this.cropTrackerRepository = cropTrackerRepository;
        this.cropRepository = cropRepository;
        this.cropTrackerMapper = cropTrackerMapper;
    }

    @Override
    public CropTrackerResponseDTO createTracker(CropTrackerRequestDTO request) {
        Crop crop = cropRepository.findById(request.getCropId())
                .orElseThrow(() -> new EntityNotFoundException("Crop not found with id: " + request.getCropId()));

        CropTracker tracker = cropTrackerMapper.toEntity(request, crop);
        CropTracker savedTracker = cropTrackerRepository.save(tracker);

        return cropTrackerMapper.toResponse(savedTracker);
    }

    @Override
    public CropTrackerResponseDTO getTrackerById(Long trackerId) {
        CropTracker tracker = cropTrackerRepository.findById(trackerId)
                .orElseThrow(() -> new EntityNotFoundException("CropTracker not found with id: " + trackerId));

        return cropTrackerMapper.toResponse(tracker);
    }

    @Override
    public List<CropTrackerResponseDTO> getAllTrackers() {
        return cropTrackerRepository.findAll().stream()
                .map(cropTrackerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CropTrackerResponseDTO> getTrackersByCropId(Long cropId) {
        return cropTrackerRepository.findByCrop_CropId(cropId).stream()
                .map(cropTrackerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CropTrackerResponseDTO> getTrackersByStatus(Status status) {
        return cropTrackerRepository.findByStatus(status).stream()
                .map(cropTrackerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CropTrackerResponseDTO> getTrackersByLogType(LogType logType) {
        return cropTrackerRepository.findByLogType(logType).stream()
                .map(cropTrackerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CropTrackerResponseDTO> getTrackersByDateRange(LocalDate startDate, LocalDate endDate) {
        return cropTrackerRepository.findByDateBetween(startDate, endDate).stream()
                .map(cropTrackerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CropTrackerResponseDTO updateTracker(Long trackerId, CropTrackerRequestDTO request) {
        CropTracker existingTracker = cropTrackerRepository.findById(trackerId)
                .orElseThrow(() -> new EntityNotFoundException("CropTracker not found with id: " + trackerId));

        Crop crop = cropRepository.findById(request.getCropId())
                .orElseThrow(() -> new EntityNotFoundException("Crop not found with id: " + request.getCropId()));

        cropTrackerMapper.updateEntityFromRequest(request, existingTracker, crop);
        CropTracker updatedTracker = cropTrackerRepository.save(existingTracker);

        return cropTrackerMapper.toResponse(updatedTracker);
    }

    @Override
    public void deleteTracker(Long trackerId) {
        if (!cropTrackerRepository.existsById(trackerId)) {
            throw new EntityNotFoundException("CropTracker not found with id: " + trackerId);
        }
        cropTrackerRepository.deleteById(trackerId);
    }
}
