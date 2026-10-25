package com.cours.backend.repository;

import com.cours.backend.model.Chapitre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapitreRepository extends JpaRepository<Chapitre, Long> {

    List<Chapitre> findByCoursIdOrderByOrdreAsc(Long coursId);

    long countByCoursId(Long coursId);
}
