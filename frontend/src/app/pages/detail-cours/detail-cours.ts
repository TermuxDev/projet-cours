import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { CoursService } from '../../services/cours.service';
import { InscriptionService } from '../../services/inscription.service';
import { ProgressionService } from '../../services/progression.service';
import { FavoriService } from '../../services/favori.service';
import { AuthService } from '../../services/auth.service';
import { CoursDetail, Participant } from '../../models/cours.model';
import { ProgressionCours } from '../../models/progression.model';

@Component({
  selector: 'app-detail-cours',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './detail-cours.html',
  styleUrl: './detail-cours.scss'
})
export class DetailCours {
  private readonly route = inject(ActivatedRoute);
  private readonly coursService = inject(CoursService);
  private readonly inscriptionService = inject(InscriptionService);
  private readonly progressionService = inject(ProgressionService);
  private readonly favoriService = inject(FavoriService);
  private readonly fb = inject(FormBuilder);
  protected readonly auth = inject(AuthService);

  protected readonly cours = signal<CoursDetail | null>(null);
  protected readonly chargement = signal(true);
  protected readonly erreur = signal<string | null>(null);
  protected readonly participants = signal<Participant[]>([]);

  protected readonly inscriptionEnCours = signal(false);
  protected readonly inscriptionReussie = signal(false);
  protected readonly erreurInscription = signal<string | null>(null);

  protected readonly progression = signal<ProgressionCours>({ pourcentage: 0, chapitresTerminesIds: [] });

  protected readonly estFavori = signal(false);
  protected readonly favoriEnCours = signal(false);

  protected readonly afficherFormulaireChapitre = signal(false);
  protected readonly ajoutEnCours = signal(false);
  protected readonly formulaireChapitre = this.fb.nonNullable.group({
    titre: ['', Validators.required],
    lienRessource: ['']
  });

  protected readonly suppressionChapitreEnCours = signal<number | null>(null);

  protected readonly afficherEdition = signal(false);
  protected readonly modificationEnCours = signal(false);
  protected readonly erreurModification = signal<string | null>(null);
  protected readonly formulaireCours = this.fb.nonNullable.group({
    titre: ['', Validators.required],
    description: [''],
    categorie: ['']
  });
  protected readonly statutEnCours = signal(false);

  protected readonly estProprietaire = computed(() => {
    const utilisateur = this.auth.utilisateurConnecte();
    const cours = this.cours();
    return !!utilisateur && !!cours && utilisateur.role === 'ENSEIGNANT' && utilisateur.id === cours.enseignantId;
  });

  constructor() {
    this.chargerCours();
  }

  private chargerCours(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    const visiteurId = this.auth.utilisateurConnecte()?.id;
    this.coursService.obtenirDetail(id, visiteurId).subscribe({
      next: (cours) => {
        this.cours.set(cours);
        this.chargement.set(false);
        this.formulaireCours.setValue({
          titre: cours.titre,
          description: cours.description ?? '',
          categorie: cours.categorie ?? ''
        });
        this.chargerParticipants(id);
        this.chargerEtatEtudiant(id);
      },
      error: () => {
        this.erreur.set('Ce cours est introuvable ou a ete depublie.');
        this.chargement.set(false);
      }
    });
  }

  private chargerParticipants(id: string): void {
    this.coursService.listerParticipants(id).subscribe((participants) => this.participants.set(participants));
  }

  private chargerEtatEtudiant(coursId: string): void {
    const utilisateur = this.auth.utilisateurConnecte();
    if (!utilisateur || utilisateur.role !== 'ETUDIANT') {
      return;
    }

    this.inscriptionService.listerPourEtudiant(utilisateur.id).subscribe((inscriptions) => {
      const estInscrit = inscriptions.some((i) => i.coursId === coursId);
      if (estInscrit) {
        this.inscriptionReussie.set(true);
        this.chargerProgression(utilisateur.id, coursId);
      }
    });

    this.favoriService.listerFavoris(utilisateur.id).subscribe((favoris) => {
      this.estFavori.set(favoris.some((c) => c.id === coursId));
    });
  }

  private chargerProgression(etudiantId: string, coursId: string): void {
    this.progressionService.obtenirProgression(etudiantId, coursId).subscribe((p) => this.progression.set(p));
  }

  sInscrire(): void {
    const utilisateur = this.auth.utilisateurConnecte();
    const cours = this.cours();
    if (!utilisateur || !cours) {
      return;
    }

    this.inscriptionEnCours.set(true);
    this.erreurInscription.set(null);

    this.inscriptionService.inscrire({ etudiantId: utilisateur.id, coursId: cours.id }).subscribe({
      next: () => {
        this.inscriptionEnCours.set(false);
        this.inscriptionReussie.set(true);
      },
      error: (reponse: HttpErrorResponse) => {
        this.inscriptionEnCours.set(false);
        if (reponse.status === 409) {
          this.inscriptionReussie.set(true);
        } else {
          this.erreurInscription.set(reponse.error?.message ?? "L'inscription a echoue.");
        }
      }
    });
  }

  basculerFavori(): void {
    const utilisateur = this.auth.utilisateurConnecte();
    const cours = this.cours();
    if (!utilisateur || !cours || this.favoriEnCours()) {
      return;
    }

    this.favoriEnCours.set(true);
    this.favoriService.basculer(utilisateur.id, cours.id).subscribe({
      next: (reponse) => {
        this.estFavori.set(reponse.actif);
        this.favoriEnCours.set(false);
      },
      error: () => this.favoriEnCours.set(false)
    });
  }

  ajouterChapitre(): void {
    const cours = this.cours();
    if (this.formulaireChapitre.invalid || !cours) {
      this.formulaireChapitre.markAllAsTouched();
      return;
    }

    this.ajoutEnCours.set(true);
    this.coursService.ajouterChapitre(cours.id, this.formulaireChapitre.getRawValue()).subscribe({
      next: () => {
        this.ajoutEnCours.set(false);
        this.afficherFormulaireChapitre.set(false);
        this.formulaireChapitre.reset({ titre: '', lienRessource: '' });
        this.chargerCours();
      },
      error: () => this.ajoutEnCours.set(false)
    });
  }

  supprimerChapitre(chapitreId: number): void {
    const cours = this.cours();
    const utilisateur = this.auth.utilisateurConnecte();
    if (!cours || !utilisateur || this.suppressionChapitreEnCours() !== null) {
      return;
    }

    this.suppressionChapitreEnCours.set(chapitreId);
    this.coursService.supprimerChapitre(cours.id, chapitreId, utilisateur.id).subscribe({
      next: () => {
        this.suppressionChapitreEnCours.set(null);
        this.chargerCours();
      },
      error: () => this.suppressionChapitreEnCours.set(null)
    });
  }

  enregistrerModification(): void {
    const cours = this.cours();
    const utilisateur = this.auth.utilisateurConnecte();
    if (this.formulaireCours.invalid || !cours || !utilisateur) {
      this.formulaireCours.markAllAsTouched();
      return;
    }

    this.modificationEnCours.set(true);
    this.erreurModification.set(null);
    this.coursService.modifierCours(cours.id, { enseignantId: utilisateur.id, ...this.formulaireCours.getRawValue() }).subscribe({
      next: () => {
        this.modificationEnCours.set(false);
        this.afficherEdition.set(false);
        this.chargerCours();
      },
      error: () => {
        this.modificationEnCours.set(false);
        this.erreurModification.set('La modification a echoue.');
      }
    });
  }

  basculerStatutCours(): void {
    const cours = this.cours();
    const utilisateur = this.auth.utilisateurConnecte();
    if (!cours || !utilisateur || this.statutEnCours()) {
      return;
    }

    this.statutEnCours.set(true);
    this.coursService.basculerStatut(cours.id, utilisateur.id).subscribe({
      next: () => {
        this.statutEnCours.set(false);
        this.chargerCours();
      },
      error: () => this.statutEnCours.set(false)
    });
  }
}
