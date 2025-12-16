package com.example.demo.Repository;

import com.example.demo.DTO.StudentMarksProjection;
import com.example.demo.Domain.MarksDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MarksRepository extends JpaRepository<MarksDomain, Long> {

    // Paginated fetch by student and semester
    Page<MarksDomain> findByStudentIdAndSemester(Long studentId, int semester, Pageable pageable);

    List<MarksDomain> findByStudentIdAndSemester(Long studentId, int semester);

    // ✅ Duplicate check using nested property path (subject.id)
    Optional<MarksDomain> findByStudentIdAndSubject_IdAndSemester(Long studentId,
                                                                  Long subjectId,
                                                                  int semester);

    // ✅ Explicit query for totals with pagination
    @Query("SELECT m.student.id AS studentId, m.student.name AS name, SUM(m.marksObtained) AS total, COUNT(m) AS count " +
            "FROM MarksDomain m GROUP BY m.student.id, m.student.name")
    Page<StudentMarksProjection> getStudentTotals(Pageable pageable);

    // ✅ Non-paged version for distribution
    @Query("SELECT m.student.id AS studentId, m.student.name AS name, SUM(m.marksObtained) AS total, COUNT(m) AS count " +
            "FROM MarksDomain m GROUP BY m.student.id, m.student.name")
    List<StudentMarksProjection> getStudentTotals();
    @Query("SELECT m.student.id AS studentId, " +
            "       m.student.name AS name, " +
            "       SUM(m.marksObtained) AS total, " +
            "       COUNT(m) AS count " +
            "FROM MarksDomain m " +
            "WHERE m.student.department.id = :deptId " +
            "GROUP BY m.student.id, m.student.name")
    Page<StudentMarksProjection> getStudentTotalsByDepartment(Long deptId, Pageable pageable);
}
