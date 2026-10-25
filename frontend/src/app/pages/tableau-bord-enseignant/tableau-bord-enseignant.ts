import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CoursService } from '../../services/cours.service';
import { Cours } from '../../models/cours.model';
import { Pagination } from '../../components/pagination/pagination';

const TAILLE_PAGE = 8;

@Component({
  selector: 'app-tableau-bord-enseignant',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, RouterLink, Pagination],
  templateUrl: './tableau-bord-enseignant.html',
  styleUrl: './tableau-bord-enseignant.scss'
})
export class TableauBordEnseignant {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly coursService = inject(CoursService);

  protected readonly mesCours = signal<Cours[]>([]);
  protected readonly chargement = signal(true);
  protected readonly enCours = signal(false);
  protected readonly erreur = signal<string | null>(null);
  protected readonly afficherFormulaire = signal(false);
  protected readonly recherche = signal('');
  protected readonly statutEnCours = signal<string | null>(null);
  protected readonly pageActuelle = signal(1);
  protected readonly taillePage = TAILLE_PAGE;

  protected readonly formulaire = this.fb.nonNullable.group({
    titre: ['', Validators.required],
    description: [''],
    categorie: ['']
  });

  protected readonly nombreActifs = computed(() => this.mesCours().filter((c) => c.actif).length);
  protected readonly nombreInscritsTotal = computed(() =>
    this.mesCours().reduce((total, c) => total + c.nombreInscrits, 0)
  );

  protected readonly coursFiltres = computed(() => {
    const terme = this.recherche().trim().toLowerCase();
    if (!terme) {
      return this.mesCours();
    }
    return this.mesCours().filter((c) => c.titre.toLowerCase().includes(terme));
  });

  protected readonly coursPage = computed(() => {
    const debut = (this.pageActuelle() - 1) * TAILLE_PAGE;
    return this.coursFiltres().slice(debut, debut + TAILLE_PAGE);
  });

  constructor() {
    this.rafraichir();
  }

  private rafraichir(): void {
    const enseignantId = this.auth.utilisateurConnecte()!.id;
    this.chargement.set(true);
    this.coursService.listerCoursParEnseignant(enseignantId).subscribe({
      next: (cours) => {
        this.mesCours.set(cours);
        this.chargement.set(false);
      },
      error: () => this.chargement.set(false)
    });
  }

  rechercher(terme: string): void {
    this.recherche.set(terme);
    this.pageActuelle.set(1);
  }

  creerCours(): void {
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }

    this.enCours.set(true);
    this.erreur.set(null);
    const enseignantId = this.auth.utilisateurConnecte()!.id;
    const valeurs = this.formulaire.getRawValue();

    this.coursService.creerCours({ enseignantId, ...valeurs }).subscribe({
      next: () => {
        this.enCours.set(false);
        this.afficherFormulaire.set(false);
        this.formulaire.reset({ titre: '', description: '', categorie: '' });
        this.rafraichir();
      },
      error: () => {
        this.enCours.set(false);
        this.erreur.set('Impossible de creer le cours pour le moment.');
      }
    });
  }

  basculerStatut(cours: Cours): void {
    if (this.statutEnCours() !== null) {
      return;
    }
    const enseignantId = this.auth.utilisateurConnecte()!.id;
    this.statutEnCours.set(cours.id);
    this.coursService.basculerStatut(cours.id, enseignantId).subscribe({
      next: () => {
        this.statutEnCours.set(null);
        this.rafraichir();
      },
      error: () => this.statutEnCours.set(null)
    });
  }
}
