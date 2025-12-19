package com.example.demo.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "student_subject_choice",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "semester"}))
public class StudentSubjectChoiceDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(name = "semester", nullable = false)
    private Integer semester;

    @ElementCollection
    @CollectionTable(name = "student_subject_choice_items", joinColumns = @JoinColumn(name = "choice_id"))
    @Column(name = "subject_id")
    private List<Long> subjectIds = new ArrayList<>();

    @Column(name = "locked", nullable = false)
    private boolean locked = true;

    @Column(name = "chosen_at", nullable = false)
    private Instant chosenAt = Instant.now();
}

