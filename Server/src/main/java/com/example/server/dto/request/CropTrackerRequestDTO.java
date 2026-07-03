package com.example.server.dto.request;

import com.example.server.enums.LogType;
import com.example.server.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropTrackerRequestDTO {

    private Long cropId;
    private String description;
    private Status status;
    private LocalDate date;
    private LogType logType;
    private Double cost;
    private String next;
    private Boolean completed;

}
