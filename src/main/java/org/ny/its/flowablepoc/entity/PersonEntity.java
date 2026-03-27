package org.ny.its.flowablepoc.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Data
@Entity
@Table(name = "person_entity")
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String email;
    private String gender;

    @Column(name = "ssn", nullable = false, unique = true)
    private String ssn;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    private String status; // IN_PROGRESS, COMPLETED
}
