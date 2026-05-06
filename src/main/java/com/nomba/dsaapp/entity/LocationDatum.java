package com.nomba.dsaapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "location_data")
public class LocationDatum {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private DsaSession session;

    @NotNull
    @Column(name = "latitude", nullable = false)
    private Long latitude;

    @NotNull
    @Column(name = "longitude", nullable = false)
    private Long longitude;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Long timestamp;


}