package com.example.demo.Domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name="Student")
public class StudentDomain {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="id", nullable = false)
        private Long id;

        @Column(name="name", nullable = false)
        private String name;

        @Column(name="dept", nullable = false)
        private String dept;

        @Temporal(TemporalType.DATE)
        @Column(name = "dob", nullable = false)
        private LocalDate dob;
}
