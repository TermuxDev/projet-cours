package com.cours.backend.repository;

import com.cours.backend.model.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoursRepository extends JpaRepository<Cours, Long> {

    Optional<Cours> findByIdentifiantPublicAndActifTrue(UUID identifiantPublic);

    // Sans filtre actif : utilise par l'enseignant/l'administrateur pour
    // gerer leurs propres cours, y compris desactives.
    Optional<Cours> findByIdentifiantPublic(UUID identifiantPublic);

    List<Cours> findByEnseignantIdOrderByDateCreationDesc(Long enseignantId);

    List<Cours> findAllByOrderByDateCreationDesc();

    // La fonction fn_NombreInscrits() est definie au niveau de la base
    // (db/procedures.sql) : le calcul du nombre d'inscrits reste une regle
    // portee par la base de donnees, Spring Boot se contente de l'exposer.
    // Seul publicId sort de la requete : la cle interne c.id ne quitte
    // jamais cette couche.
    @Query(value = """
            SELECT c.publicId AS identifiantPublic, c.titre AS titre, c.description AS description, c.categorie AS categorie,
                   (u.prenom + ' ' + u.nom) AS enseignantNom,
                   dbo.fn_NombreInscrits(c.id) AS nombreInscrits,
                   (SELECT COUNT(*) FROM chapitres ch WHERE ch.coursId = c.id) AS nombreChapitres
            FROM cours c
            JOIN utilisateurs u ON u.id = c.enseignantId
            WHERE c.actif = 1
            ORDER BY c.dateCreation DESC
            """, nativeQuery = true)
    List<CoursListeProjection> listerCoursActifs();
}
