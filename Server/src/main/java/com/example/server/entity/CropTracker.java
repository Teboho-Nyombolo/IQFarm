package com.example.server.entity;

import com.example.server.enums.LogType;
import com.example.server.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "crop_trackers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tracker_id")
    private Long trackerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "log_type", nullable = false)
    private LogType logType;

    private Double cost;

    private String next;

    @Column(nullable = false)
    private Boolean completed;

}
