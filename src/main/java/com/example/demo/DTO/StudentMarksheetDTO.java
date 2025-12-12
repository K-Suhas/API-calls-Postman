// src/main/java/com/example/demo/DTO/StudentMarksheetDTO.java
package com.example.demo.DTO;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class StudentMarksheetDTO {
    private Long id;
    private String name;
    private String departmentName;

    private String email;
    private LocalDate dob;
    // If a student has multiple courses, we store all names
    private List<String> courseNames;

    private int totalMarks;
    private double percentage;

    // Detailed subjects (name + marks)
    private List<MarksDTO> subjects;
}
