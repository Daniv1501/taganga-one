package com.taganga.one.repository;

import com.taganga.one.domain.AssistantEntry;
import com.taganga.one.domain.AssistantEntryType;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssistantEntryRepository extends JpaRepository<AssistantEntry, Long> {

    @EntityGraph(attributePaths = "keywords")
    List<AssistantEntry> findByTypeOrderByDisplayOrderAsc(AssistantEntryType type);
}
