package com.taganga.one.repository;

import com.taganga.one.domain.DocumentType;
import com.taganga.one.domain.Visitor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Optional<Visitor> findByDocumentTypeAndDocumentNumber(
            DocumentType documentType, String documentNumber);

    Optional<Visitor> findByVisitorCode(String visitorCode);

    @Query(value = "select nextval('visitor_code_seq')", nativeQuery = true)
    long nextVisitorCodeNumber();
}
