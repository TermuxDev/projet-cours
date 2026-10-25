package com.cours.backend.repository;

import com.cours.backend.model.Favori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FavoriRepository extends JpaRepository<Favori, Long> {

    boolean existsByEtudiantIdAndCoursId(Long etudiantId, Long coursId);

    List<Favori> findByEtudiantId(Long etudiantId);

    @Modifying
    @Transactional
    void deleteByEtudiantIdAndCoursId(Long etudiantId, Long coursId);
}
