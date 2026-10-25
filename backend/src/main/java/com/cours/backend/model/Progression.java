package com.cours.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "progressions", uniqueConstraints = @UniqueConstraint(columnNames = {"etudiantId", "chapitreId"}))
public class Progression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiantId", nullable = false)
    private Utilisateur etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitreId", nullable = false)
    private Chapitre chapitre;

    @Column(name = "dateCompletion", nullable = false, updatable = false, insertable = false)
    private LocalDateTime dateCompletion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getEtudiant() { return etudiant; }
    public void setEtudiant(Utilisateur etudiant) { this.etudiant = etudiant; }

    public Chapitre getChapitre() { return chapitre; }
    public void setChapitre(Chapitre chapitre) { this.chapitre = chapitre; }

    public LocalDateTime getDateCompletion() { return dateCompletion; }
}
