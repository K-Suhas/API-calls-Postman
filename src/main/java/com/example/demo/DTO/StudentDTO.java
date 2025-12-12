// src/main/java/com/example/demo/DTO/StudentDTO.java
package com.example.demo.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class StudentDTO {

    private Long id;   // ✅ Added: needed for update, mapper, etc.

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    // Keep dob as String for frontend binding
    @NotBlank(message = "DOB is required")
    private String dob;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private Long departmentId;          // used by backend
    private String departmentName;      // convenience for frontend

    private List<String> courseNames;
}
