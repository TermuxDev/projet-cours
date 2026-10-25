package com.cours.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "chapitres")
public class Chapitre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coursId", nullable = false)
    private Cours cours;

    @Column(name = "titre", nullable = false)
    private String titre;

    @Column(name = "ordre", nullable = false)
    private int ordre;

    @Column(name = "lienRessource")
    private String lienRessource;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cours getCours() { return cours; }
    public void setCours(Cours cours) { this.cours = cours; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }

    public String getLienRessource() { return lienRessource; }
    public void setLienRessource(String lienRessource) { this.lienRessource = lienRessource; }
}
