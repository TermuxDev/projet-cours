package com.cours.backend.repository;

import java.util.UUID;

public interface CoursListeProjection {
    UUID getIdentifiantPublic();
    String getTitre();
    String getDescription();
    String getCategorie();
    String getEnseignantNom();
    long getNombreInscrits();
    long getNombreChapitres();
}
