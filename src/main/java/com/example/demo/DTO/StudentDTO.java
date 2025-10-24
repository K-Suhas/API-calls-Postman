package com.example.demo.DTO;

import lombok.*;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class StudentDTO {
    private Long id;
    private String name;
    private String dept;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dob;
}
