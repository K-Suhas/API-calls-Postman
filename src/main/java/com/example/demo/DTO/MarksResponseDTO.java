package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MarksResponseDTO {
    private List<MarksDTO> subjects;
    private int total;
    private double percentage;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
}


