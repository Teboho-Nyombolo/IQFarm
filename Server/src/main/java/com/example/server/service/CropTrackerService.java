package com.example.server.service;

import com.example.server.dto.request.CropTrackerRequestDTO;
import com.example.server.dto.response.CropTrackerResponseDTO;
import com.example.server.enums.LogType;
import com.example.server.enums.Status;

import java.time.LocalDate;
import java.util.List;

public interface CropTrackerService {

    CropTrackerResponseDTO createTracker(CropTrackerRequestDTO request);

    CropTrackerResponseDTO getTrackerById(Long trackerId);

    List<CropTrackerResponseDTO> getAllTrackers();

    List<CropTrackerResponseDTO> getTrackersByCropId(Long cropId);

    List<CropTrackerResponseDTO> getTrackersByStatus(Status status);

    List<CropTrackerResponseDTO> getTrackersByLogType(LogType logType);

    List<CropTrackerResponseDTO> getTrackersByDateRange(LocalDate startDate, LocalDate endDate);

    CropTrackerResponseDTO updateTracker(Long trackerId, CropTrackerRequestDTO request);

    void deleteTracker(Long trackerId);

}
