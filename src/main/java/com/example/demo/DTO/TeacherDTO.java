// src/main/java/com/example/demo/DTO/TeacherDTO.java
package com.example.demo.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {

    private Long id;
    private String name;
    private String email;

    private Long departmentId;
    private String departmentName;
}
