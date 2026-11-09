package com.taganga.one.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assistant_entries")
public class AssistantEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AssistantEntryType type;

    @Column(length = 200)
    private String question;

    @Column(nullable = false, columnDefinition = "text")
    private String answer;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @ElementCollection
    @CollectionTable(name = "assistant_keywords", joinColumns = @JoinColumn(name = "entry_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "keyword", nullable = false, length = 50)
    private List<String> keywords = new ArrayList<>();

    protected AssistantEntry() {
    }

    public AssistantEntry(String code, AssistantEntryType type, String question, String answer,
            int displayOrder, List<String> keywords) {
        this.code = code;
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.displayOrder = displayOrder;
        this.keywords = new ArrayList<>(keywords);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public AssistantEntryType getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public List<String> getKeywords() {
        return keywords;
    }
}
