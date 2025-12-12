// src/main/java/com/example/demo/Domain/DepartmentDomain.java
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
@Table(name = "Department")
public class DepartmentDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g., "CSE", "ISE", "ECE", "MECH" or full names
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "department")
    private Set<StudentDomain> students = new HashSet<>();

    @OneToMany(mappedBy = "department")
    private Set<TeacherDomain> teachers = new HashSet<>();

    @OneToMany(mappedBy = "department")
    private Set<CourseDomain> courses = new HashSet<>();
}
