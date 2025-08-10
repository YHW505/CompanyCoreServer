package com.example.companycoreserver.entity.Enum;

public enum ApprovalStatus {
    PENDING("대기중"),
    APPROVED("승인"),
    REJECTED("거부");

    private final String description;

    ApprovalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
