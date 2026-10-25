package com.cours.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cours")
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "publicId", nullable = false, updatable = false, insertable = false)
    private UUID identifiantPublic;

    @Column(name = "titre", nullable = false)
    private String titre;

    @Column(name = "description")
    private String description;

    @Column(name = "categorie")
    private String categorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enseignantId", nullable = false)
    private Utilisateur enseignant;

    @Column(name = "dateCreation", nullable = false, updatable = false, insertable = false)
    private LocalDateTime dateCreation;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getIdentifiantPublic() { return identifiantPublic; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public Utilisateur getEnseignant() { return enseignant; }
    public void setEnseignant(Utilisateur enseignant) { this.enseignant = enseignant; }

    public LocalDateTime getDateCreation() { return dateCreation; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
