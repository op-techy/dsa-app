package com.nomba.dsaapp.repository;

import com.nomba.dsaapp.entity.Dsa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DsaRepository extends JpaRepository<Dsa, UUID> {
}