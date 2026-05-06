package com.nomba.dsaapp.repository;

import com.nomba.dsaapp.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CheckoutRepository extends JpaRepository<Checkout, UUID> {
}