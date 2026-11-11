package com.taganga.one.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "visitors")
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visitor_code", nullable = false, unique = true, length = 20)
    private String visitorCode;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @Column(nullable = false, length = 120)
    private String origin;

    @Column(name = "group_size", nullable = false)
    private int groupSize;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VisitReason reason;

    @OneToMany(mappedBy = "visitor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DataConsent> consents = new ArrayList<>();

    protected Visitor() {
    }

    public Visitor(String visitorCode, String fullName, String email, String passwordHash,
            String origin, int groupSize, LocalDate visitDate, VisitReason reason) {
        this.visitorCode = visitorCode;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.origin = origin;
        this.groupSize = groupSize;
        this.visitDate = visitDate;
        this.reason = reason;
    }

    // Si el correo ya estaba registrado (y la contraseña coincide), el registro
    // nuevo reemplaza al anterior y el visitante recibe un código nuevo.
    public void replaceRegistration(String visitorCode, String fullName,
            String origin, int groupSize, LocalDate visitDate, VisitReason reason) {
        this.visitorCode = visitorCode;
        this.fullName = fullName;
        this.origin = origin;
        this.groupSize = groupSize;
        this.visitDate = visitDate;
        this.reason = reason;
    }

    public void recordConsent(String legalBasis, LocalDateTime acceptedAt) {
        consents.add(new DataConsent(this, legalBasis, acceptedAt));
    }

    public Long getId() {
        return id;
    }

    public String getVisitorCode() {
        return visitorCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getOrigin() {
        return origin;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public VisitReason getReason() {
        return reason;
    }

    public List<DataConsent> getConsents() {
        return Collections.unmodifiableList(consents);
    }
}
