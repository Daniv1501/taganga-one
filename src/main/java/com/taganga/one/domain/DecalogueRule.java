package com.taganga.one.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "decalogue_rules")
public class DecalogueRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_order", nullable = false, unique = true)
    private int ruleOrder;

    @Column(nullable = false, length = 120)
    private String highlight;

    @Column(nullable = false)
    private String body;

    protected DecalogueRule() {
    }

    public DecalogueRule(int ruleOrder, String highlight, String body) {
        this.ruleOrder = ruleOrder;
        this.highlight = highlight;
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public int getRuleOrder() {
        return ruleOrder;
    }

    public String getHighlight() {
        return highlight;
    }

    public String getBody() {
        return body;
    }
}
