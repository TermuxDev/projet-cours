package com.cours.backend.repository;

import com.cours.backend.model.Progression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProgressionRepository extends JpaRepository<Progression, Long> {

    boolean existsByEtudiantIdAndChapitreId(Long etudiantId, Long chapitreId);

    List<Progression> findByEtudiantIdAndChapitre_CoursId(Long etudiantId, Long coursId);

    @Modifying
    @Transactional
    void deleteByEtudiantIdAndChapitreId(Long etudiantId, Long chapitreId);

    // Delegue a sp_MarquerChapitreComplete (db/procedures.sql) la regle
    // "il faut etre inscrit au cours pour valider sa progression".
    @Modifying
    @Transactional
    @Query(value = "EXEC sp_MarquerChapitreComplete :etudiantId, :chapitreId", nativeQuery = true)
    void marquerCompleteViaProcedure(Long etudiantId, Long chapitreId);

    @Query(value = "SELECT dbo.fn_PourcentageProgression(:etudiantId, :coursId)", nativeQuery = true)
    int calculerPourcentage(Long etudiantId, Long coursId);
}
