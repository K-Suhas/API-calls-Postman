package com.example.demo.Mapper;

import com.example.demo.DTO.MarksDTO;
import com.example.demo.Domain.MarksDomain;

public class MarksMapper {
    public static MarksDTO toDTO(MarksDomain marks) {
        return new MarksDTO(marks.getSubjectName(), marks.getMarksObtained());
    }
}

