package com.example.demo.Domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Table(name = "Marks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "subjectName", "semester"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MarksDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private StudentDomain student;

    @Column(nullable = false)
    private String subjectName; // DBMS, DAA, etc.

    @Column(nullable = false)
    private int marksObtained; // 0–100

    @Column(nullable = false)
    private int semester;
}
