package com.example.server.mapper;

import com.example.server.dto.request.CropTrackerRequestDTO;
import com.example.server.dto.response.CropTrackerResponseDTO;
import com.example.server.entity.Crop;
import com.example.server.entity.CropTracker;
import org.springframework.stereotype.Component;

@Component
public class CropTrackerMapper {

    public CropTracker toEntity(CropTrackerRequestDTO request, Crop crop) {
        return CropTracker.builder()
                .crop(crop)
                .description(request.getDescription())
                .status(request.getStatus())
                .date(request.getDate())
                .logType(request.getLogType())
                .cost(request.getCost())
                .next(request.getNext())
                .completed(request.getCompleted())
                .build();
    }

    public CropTrackerResponseDTO toResponse(CropTracker tracker) {
        return CropTrackerResponseDTO.builder()
                .trackerId(tracker.getTrackerId())
                .cropId(tracker.getCrop().getCropId())
                .cropName(tracker.getCrop().getCropName())
                .description(tracker.getDescription())
                .status(tracker.getStatus())
                .date(tracker.getDate())
                .logType(tracker.getLogType())
                .cost(tracker.getCost())
                .next(tracker.getNext())
                .completed(tracker.getCompleted())
                .build();
    }

    public void updateEntityFromRequest(CropTrackerRequestDTO request, CropTracker tracker, Crop crop) {
        tracker.setCrop(crop);
        tracker.setDescription(request.getDescription());
        tracker.setStatus(request.getStatus());
        tracker.setDate(request.getDate());
        tracker.setLogType(request.getLogType());
        tracker.setCost(request.getCost());
        tracker.setNext(request.getNext());
        tracker.setCompleted(request.getCompleted());
    }
}


