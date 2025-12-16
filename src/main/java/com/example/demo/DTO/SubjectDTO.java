// src/main/java/com/example/demo/DTO/SubjectDTO.java
package com.example.demo.DTO;

import lombok.*;
import lombok.experimental.Accessors;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Accessors(chain = true)
public class SubjectDTO {
    private Long id;
    private String name;
    private int semester;
    private Long departmentId;
    private String departmentName;
}
