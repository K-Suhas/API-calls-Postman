package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class SubjectChoiceRequestDTO {
    private Long studentId;
    private Long departmentId;
    private Integer semester;
    private List<Long> subjectIds; // must be exactly 4
}
