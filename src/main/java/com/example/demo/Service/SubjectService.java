// src/main/java/com/example/demo/Service/SubjectService.java
package com.example.demo.Service;

import com.example.demo.DTO.SubjectDTO;

import java.util.List;

public interface SubjectService {
    String createSubject(SubjectDTO dto);
    List<SubjectDTO> getSubjectsByDepartmentAndSemester(Long departmentId, int semester);
    List<SubjectDTO> getSubjectsForStudent(Long studentId, int semester);
    String updateSubject(Long id, SubjectDTO dto);
    String deleteSubject(Long id);

}
