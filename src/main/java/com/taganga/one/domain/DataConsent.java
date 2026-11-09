package com.taganga.one.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Prueba de la autorización de tratamiento de datos (Ley 1581 de 2012).
 */
@Entity
@Table(name = "data_consents")
public class DataConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visitor_id", nullable = false)
    private Visitor visitor;

    @Column(name = "legal_basis", nullable = false, length = 100)
    private String legalBasis;

    @Column(name = "accepted_at", nullable = false)
    private LocalDateTime acceptedAt;

    protected DataConsent() {
    }

    DataConsent(Visitor visitor, String legalBasis, LocalDateTime acceptedAt) {
        this.visitor = visitor;
        this.legalBasis = legalBasis;
        this.acceptedAt = acceptedAt;
    }

    public Long getId() {
        return id;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public String getLegalBasis() {
        return legalBasis;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }
}
