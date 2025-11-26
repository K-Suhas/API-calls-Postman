// StudentReportRowDTO.java
package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StudentReportRowDTO {
    private Long id;
    private String name;
    private String dept;
    private String email;
    private LocalDate dob;
    private List<String> courseNames;
    private int subjectsCount;
    private int totalMarks;
    private double percentage;
}
