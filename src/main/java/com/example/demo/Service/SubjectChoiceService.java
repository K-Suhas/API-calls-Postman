package com.example.demo.Service;

import com.example.demo.DTO.SubjectChoiceRequestDTO;
import com.example.demo.DTO.SubjectChoiceResponseDTO;

import java.util.Optional;

public interface SubjectChoiceService {
    SubjectChoiceResponseDTO createChoice(SubjectChoiceRequestDTO req);
    Optional<SubjectChoiceResponseDTO> getChoice(Long studentId, Integer semester);
}
