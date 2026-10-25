package com.cours.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identifiant public (UUID genere par la base, cf. db/tables.sql) :
    // seul identifiant expose dans l'API, jamais la cle interne sequentielle.
    @Column(name = "publicId", nullable = false, updatable = false, insertable = false)
    private UUID identifiantPublic;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "motDePasse", nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleUtilisateur role;

    @Column(name = "dateCreation", nullable = false, updatable = false, insertable = false)
    private LocalDateTime dateCreation;

    @Column(name = "emailVerifie", nullable = false)
    private boolean emailVerifie;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getIdentifiantPublic() { return identifiantPublic; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public RoleUtilisateur getRole() { return role; }
    public void setRole(RoleUtilisateur role) { this.role = role; }

    public LocalDateTime getDateCreation() { return dateCreation; }

    public boolean isEmailVerifie() { return emailVerifie; }
    public void setEmailVerifie(boolean emailVerifie) { this.emailVerifie = emailVerifie; }
}
