package com.cours.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favoris", uniqueConstraints = @UniqueConstraint(columnNames = {"etudiantId", "coursId"}))
public class Favori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiantId", nullable = false)
    private Utilisateur etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coursId", nullable = false)
    private Cours cours;

    @Column(name = "dateAjout", nullable = false, updatable = false, insertable = false)
    private LocalDateTime dateAjout;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getEtudiant() { return etudiant; }
    public void setEtudiant(Utilisateur etudiant) { this.etudiant = etudiant; }

    public Cours getCours() { return cours; }
    public void setCours(Cours cours) { this.cours = cours; }

    public LocalDateTime getDateAjout() { return dateAjout; }
}
