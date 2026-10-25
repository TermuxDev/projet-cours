package com.cours.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inscriptions", uniqueConstraints = @UniqueConstraint(columnNames = {"etudiantId", "coursId"}))
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "publicId", nullable = false, updatable = false, insertable = false)
    private UUID identifiantPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiantId", nullable = false)
    private Utilisateur etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coursId", nullable = false)
    private Cours cours;

    @Column(name = "dateInscription", nullable = false, updatable = false, insertable = false)
    private LocalDateTime dateInscription;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getIdentifiantPublic() { return identifiantPublic; }

    public Utilisateur getEtudiant() { return etudiant; }
    public void setEtudiant(Utilisateur etudiant) { this.etudiant = etudiant; }

    public Cours getCours() { return cours; }
    public void setCours(Cours cours) { this.cours = cours; }

    public LocalDateTime getDateInscription() { return dateInscription; }
}
