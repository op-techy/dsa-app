package com.nomba.dsaapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "checkout")
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private DsaSession session;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Long endTime;

    @Column(name = "total_distance_m")
    private Long totalDistanceM;

    @Column(name = "total_time_s")
    private Long totalTimeS;


}