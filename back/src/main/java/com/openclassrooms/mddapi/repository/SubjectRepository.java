package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
