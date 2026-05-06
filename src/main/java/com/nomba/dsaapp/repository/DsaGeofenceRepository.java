package com.nomba.dsaapp.repository;

import com.nomba.dsaapp.entity.DsaGeofence;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.UUID;

public interface DsaGeofenceRepository extends JpaRepository<DsaGeofence, UUID> {
    List<DsaGeofence> findByDsaId(UUID id);
}