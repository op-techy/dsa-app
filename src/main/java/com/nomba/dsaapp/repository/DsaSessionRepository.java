package com.nomba.dsaapp.repository;

import com.nomba.dsaapp.entity.DsaSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DsaSessionRepository extends JpaRepository<DsaSession, UUID> {
    Optional<DsaSession> findByDsaIdAndCheckoutIsNull(UUID dsaId);
}