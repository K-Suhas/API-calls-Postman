// src/main/java/com/example/demo/Domain/MarksDomain.java
package com.example.demo.Domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Table(name = "Marks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "subject_id", "semester"})
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id")
    private SubjectDomain subject;

    @Column(nullable = false)
    private int marksObtained;

    @Column(nullable = false)
    private int semester;
}
