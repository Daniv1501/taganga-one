package com.taganga.one.repository;

import com.taganga.one.domain.DecalogueRule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DecalogueRuleRepository extends JpaRepository<DecalogueRule, Long> {

    List<DecalogueRule> findAllByOrderByRuleOrderAsc();
}
