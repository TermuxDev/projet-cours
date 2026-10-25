package com.cours.backend.repository;

import com.cours.backend.model.CodeVerification;
import com.cours.backend.model.TypeCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeVerificationRepository extends JpaRepository<CodeVerification, Long> {

    Optional<CodeVerification> findTopByUtilisateurIdAndTypeAndUtiliseFalseOrderByDateCreationDesc(
            Long utilisateurId, TypeCode type);
}
