package com.example.demo.DTO;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MarksDTO {
    private String subjectName;
    private int marksObtained;
}
