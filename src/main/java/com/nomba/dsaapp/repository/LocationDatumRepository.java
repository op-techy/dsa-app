package com.nomba.dsaapp.repository;

import com.nomba.dsaapp.entity.LocationDatum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LocationDatumRepository extends JpaRepository<LocationDatum, UUID> {
    List<LocationDatum> findBySessionId(UUID sessionId);
}