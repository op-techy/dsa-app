package com.nomba.dsaapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "geofence")
public class Geofence {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "latitude", nullable = false)
    private Long latitude;

    @NotNull
    @Column(name = "longitude", nullable = false)
    private Long longitude;

    @NotNull
    @Column(name = "radius_in_metres", nullable = false)
    private Long radiusInMetres;


}