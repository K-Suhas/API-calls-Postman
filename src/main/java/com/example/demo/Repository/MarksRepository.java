package com.example.demo.Repository;

import com.example.demo.DTO.StudentMarksProjection;
import com.example.demo.Domain.MarksDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MarksRepository extends JpaRepository<MarksDomain, Long> {

    // Paginated fetch by student and semester
    Page<MarksDomain> findByStudentIdAndSemester(Long studentId, int semester, Pageable pageable);
    List<MarksDomain> findByStudentIdAndSemester(Long studentId, int semester);
    @Query("SELECT m.student.id AS studentId, m.student.name AS name, SUM(m.marksObtained) AS total, COUNT(m) AS count " +
            "FROM MarksDomain m GROUP BY m.student.id, m.student.name")
    Page<StudentMarksProjection> getStudentTotals(Pageable pageable);


    // ✅ For duplicate check (single result)
    Optional<MarksDomain> findByStudentIdAndSubjectNameAndSemester(Long studentId, String subjectName, int semester);


}
