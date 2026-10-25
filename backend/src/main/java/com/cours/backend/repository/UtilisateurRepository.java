package com.cours.backend.repository;

import com.cours.backend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    Optional<Utilisateur> findByIdentifiantPublic(UUID identifiantPublic);

    boolean existsByEmail(String email);
}
