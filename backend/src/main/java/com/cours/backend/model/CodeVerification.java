package com.cours.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "codes_verification")
public class CodeVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateurId", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "code", nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeCode type;

    @Column(name = "dateExpiration", nullable = false)
    private LocalDateTime dateExpiration;

    @Column(name = "dateCreation", nullable = false, updatable = false, insertable = false)
    private LocalDateTime dateCreation;

    @Column(name = "utilise", nullable = false)
    private boolean utilise;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public TypeCode getType() { return type; }
    public void setType(TypeCode type) { this.type = type; }

    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDateTime dateExpiration) { this.dateExpiration = dateExpiration; }

    public LocalDateTime getDateCreation() { return dateCreation; }

    public boolean isUtilise() { return utilise; }
    public void setUtilise(boolean utilise) { this.utilise = utilise; }
}
