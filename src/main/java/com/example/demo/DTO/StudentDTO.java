package com.example.demo.DTO;

import lombok.*;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class StudentDTO {
    private Long id;
    private String name;
    private String dept;// input from ing courses)private List<String> courseNames;
    // For output (displaying enrolled courses)

    private List<String> courseNames; // input from user

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;
}
