package com.cours.backend.repository;

import com.cours.backend.model.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    boolean existsByEtudiantIdAndCoursId(Long etudiantId, Long coursId);

    Optional<Inscription> findByEtudiantIdAndCoursId(Long etudiantId, Long coursId);

    List<Inscription> findByEtudiantId(Long etudiantId);

    List<Inscription> findByCoursId(Long coursId);

    long countByCoursId(Long coursId);

    // Delegue la regle "pas de double inscription / cours inactif" a la
    // procedure stockee sp_InscrireEtudiant (db/procedures.sql).
    @Modifying
    @Transactional
    @Query(value = "EXEC sp_InscrireEtudiant :etudiantId, :coursId", nativeQuery = true)
    void inscrireViaProcedure(Long etudiantId, Long coursId);
}
