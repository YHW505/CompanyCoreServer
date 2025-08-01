package com.example.companycoreserver.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "positions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Integer positionId;

    @Column(name = "position_code", length = 10, unique = true, nullable = false)
    private String positionCode;

    @Column(name = "position_name", length = 50, nullable = false)
    private String positionName;

    @Column(name = "level_order", nullable = false)
    private Integer levelOrder;

    // 🔗 관계 매핑
    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users = new ArrayList<>();

    // 기본 생성자
    public Position() {}

    // 생성자
    public Position(String positionCode, String positionName, Integer levelOrder) {
        this.positionCode = positionCode;
        this.positionName = positionName;
        this.levelOrder = levelOrder;
    }

    // Getter/Setter
    public Integer getPositionId() { return positionId; }
    public void setPositionId(Integer positionId) { this.positionId = positionId; }

    public String getPositionCode() { return positionCode; }
    public void setPositionCode(String positionCode) { this.positionCode = positionCode; }

    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }

    public Integer getLevelOrder() { return levelOrder; }
    public void setLevelOrder(Integer levelOrder) { this.levelOrder = levelOrder; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    @Override
    public String toString() {
        return "Position{" +
                "positionId=" + positionId +
                ", positionCode='" + positionCode + '\'' +
                ", positionName='" + positionName + '\'' +
                ", levelOrder=" + levelOrder +
                '}';
    }
}
