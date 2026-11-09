package com.taganga.one.domain;

public enum DocumentType {
    CC,
    CE,
    TI,
    PA;

    // Pasaporte: alfanumérico. Los demás documentos: solo dígitos.
    public boolean accepts(String number) {
        return number != null && number.matches(
                this == PA ? "[A-Za-z0-9]{5,12}" : "\\d{5,12}"
        );
    }
}
