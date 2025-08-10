package com.example.companycoreserver.dto;

public class DeptPosiUpdateRequest {
    private Integer departmentId;
    private Integer positionId;

    // getter, setter
    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
    public Integer getPositionId() { return positionId; }
    public void setPositionId(Integer positionId) { this.positionId = positionId; }
}
