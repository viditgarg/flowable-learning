package org.ny.its.flowablepoc.repository;

import org.ny.its.flowablepoc.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
}
