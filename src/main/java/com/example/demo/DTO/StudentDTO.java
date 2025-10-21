package com.example.demo.DTO;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor

@Getter
@Setter
@Accessors(chain = true)

public class StudentDTO
{
    private Long id;
    private String name;
    private String dept;
    private Date dob;
}



