package com.example.demo.Repository;

import com.example.demo.Domain.StudentDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<StudentDomain, Long> {

    // Search by id, name, or department (null-safe)
    // Search only by id, name, or department (no courses)
    @Query("""
   SELECT s FROM StudentDomain s 
   WHERE (:id IS NOT NULL AND s.id = :id)
      OR (:id IS NULL AND (
            LOWER(s.name) = LOWER(:query)
         OR LOWER(s.department.name) = LOWER(:query)
      ))
   """)
    Page<StudentDomain> searchByIdOrNameOrDept(@Param("id") Long id,
                                               @Param("query") String query,
                                               Pageable pageable);



    @Query(value = "SELECT * FROM Student WHERE DATE(dob) = :dob", nativeQuery = true)
    Page<StudentDomain> searchByDob(@Param("dob") LocalDate dob, Pageable pageable);

    Optional<StudentDomain> findByEmail(String email);

    @Query("SELECT DISTINCT s FROM StudentDomain s LEFT JOIN FETCH s.courses")
    List<StudentDomain> findAllWithCourses();

    @Query("""
           SELECT s FROM StudentDomain s 
           WHERE s.name = :name 
             AND s.dob = :dob 
             AND LOWER(s.department.name) = LOWER(:departmentName)
           """)
    Optional<StudentDomain> findExistingStudent(@Param("name") String name,
                                                @Param("dob") LocalDate dob,
                                                @Param("departmentName") String departmentName);

    Page<StudentDomain> findByDepartment_Id(Long deptId, Pageable pageable);

}
