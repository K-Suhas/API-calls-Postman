package com.example.demo.DTO;

public interface StudentMarksProjection {
    Long getStudentId();
    String getName();
    Integer getTotal();
    Integer getCount(); // subject count
}
