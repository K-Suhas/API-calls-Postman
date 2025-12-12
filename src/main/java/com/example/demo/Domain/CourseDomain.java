// src/main/java/com/example/demo/Domain/CourseDomain.java
package com.example.demo.Domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name="Course")
public class CourseDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="name", nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentDomain department;

    @ManyToMany(mappedBy = "courses")
    private Set<StudentDomain> students = new HashSet<>();
}
