// src/main/java/com/example/demo/DTO/MarksEntryRequestDTO.java
package com.example.demo.DTO;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MarksEntryRequestDTO {
    private Long studentId;
    private int semester;
    private List<MarksDTO> subjects;
}
