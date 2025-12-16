// src/main/java/com/example/demo/Domain/SubjectDomain.java
package com.example.demo.Domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Table(name = "Subject", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "department_id", "semester"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SubjectDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "Operating Systems"

    @Column(nullable = false)
    private int semester; // 1–8

    @ManyToOne(optional = false)
    @JoinColumn(name = "department_id")
    private DepartmentDomain department;
}
