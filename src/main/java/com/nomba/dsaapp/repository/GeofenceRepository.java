package com.nomba.dsaapp.repository;

import com.nomba.dsaapp.entity.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GeofenceRepository extends JpaRepository<Geofence, UUID> {
}