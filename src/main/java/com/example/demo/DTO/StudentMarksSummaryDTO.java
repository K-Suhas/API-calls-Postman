package com.example.demo.DTO;

import lombok.Data;

@Data
public class StudentMarksSummaryDTO {
    private Long studentId;
    private String name;
    private int total;
    private double percentage;

    public StudentMarksSummaryDTO(Long studentId, String name, int total, int subjectCount) {
        this.studentId = studentId;
        this.name = name;
        this.total = total;
        this.percentage = subjectCount > 0 ? total / (subjectCount * 1.0) : 0.0;
    }
}
