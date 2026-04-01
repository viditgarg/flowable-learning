package org.ny.its.flowablepoc.repository;

import org.ny.its.flowablepoc.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    // ✅ Optional: find by SSN
    Optional<PersonEntity> findBySsn(String ssn);

    // ✅ Add this line for existence check
    boolean existsBySsn(String ssn);
}
