package com.nomba.dsaapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "dsa_session")
public class DsaSession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dsa_id", nullable = false)
    private Dsa dsa;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Long startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkout_id")
    private Checkout checkout;


}