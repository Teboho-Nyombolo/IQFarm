package com.example.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "farms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "farm_id")
    private Long farmId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_owner", nullable = false, unique = true)
    private User farmOwner;

    @Column(name = "farm_name", nullable = false)
    private String farmName;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Crop> crops;

}
