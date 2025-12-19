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
public class SubjectChoiceResponseDTO {
    private Long studentId;
    private Integer semester;
    private Long departmentId;
    private List<Long> subjectIds;
    private boolean locked;
}
