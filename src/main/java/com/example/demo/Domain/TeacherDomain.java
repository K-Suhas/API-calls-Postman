// src/main/java/com/example/demo/Domain/TeacherDomain.java
package com.example.demo.Domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "Teacher")
public class TeacherDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentDomain department;
}
