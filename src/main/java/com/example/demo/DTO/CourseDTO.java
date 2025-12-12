// src/main/java/com/example/demo/DTO/CourseDTO.java
package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class CourseDTO {

    private Long id;

    @NotBlank(message = "Course name must not be blank")
    private String name;

    // ✅ Required on create; locked on update
    private Long departmentId;
    private String departmentName;

    private List<String> studentNames;
}
