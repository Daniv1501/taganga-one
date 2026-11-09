package com.taganga.one.repository;

import com.taganga.one.domain.Zone;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    @EntityGraph(attributePaths = "coordinates")
    List<Zone> findAllByOrderByDisplayOrderAsc();
}
