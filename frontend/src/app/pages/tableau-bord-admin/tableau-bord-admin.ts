import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CoursService } from '../../services/cours.service';
import { UtilisateurService } from '../../services/utilisateur.service';
import { Cours } from '../../models/cours.model';
import { RoleUtilisateur, Utilisateur } from '../../models/utilisateur.model';
import { Pagination } from '../../components/pagination/pagination';

type Onglet = 'apercu' | 'utilisateurs' | 'cours';
type FiltreRole = RoleUtilisateur | 'TOUS';
type FiltreStatut = 'TOUS' | 'ACTIFS' | 'INACTIFS';

const TAILLE_PAGE = 8;

@Component({
  selector: 'app-tableau-bord-admin',
  standalone: true,
  imports: [FormsModule, DatePipe, Pagination],
  templateUrl: './tableau-bord-admin.html',
  styleUrl: './tableau-bord-admin.scss'
})
export class TableauBordAdmin {
  private readonly auth = inject(AuthService);
  private readonly coursService = inject(CoursService);
  private readonly utilisateurService = inject(UtilisateurService);

  protected readonly onglet = signal<Onglet>('apercu');

  protected readonly utilisateurs = signal<Utilisateur[]>([]);
  protected readonly cours = signal<Cours[]>([]);
  protected readonly chargement = signal(true);
  protected readonly statutEnCours = signal<string | null>(null);

  protected readonly rechercheUtilisateurs = signal('');
  protected readonly filtreRole = signal<FiltreRole>('TOUS');
  protected readonly pageUtilisateurs = signal(1);

  protected readonly rechercheCours = signal('');
  protected readonly filtreStatut = signal<FiltreStatut>('TOUS');
  protected readonly pageCours = signal(1);

  protected readonly taillePage = TAILLE_PAGE;

  protected readonly nombreEtudiants = computed(
    () => this.utilisateurs().filter((u) => u.role === 'ETUDIANT').length
  );
  protected readonly nombreEnseignants = computed(
    () => this.utilisateurs().filter((u) => u.role === 'ENSEIGNANT').length
  );
  protected readonly nombreAdmins = computed(
    () => this.utilisateurs().filter((u) => u.role === 'ADMINISTRATEUR').length
  );
  protected readonly nombreCoursActifs = computed(() => this.cours().filter((c) => c.actif).length);
  protected readonly nombreCoursInactifs = computed(() => this.cours().filter((c) => !c.actif).length);
  protected readonly nombreInscriptions = computed(() =>
    this.cours().reduce((total, c) => total + c.nombreInscrits, 0)
  );

  protected readonly utilisateursFiltres = computed(() => {
    const terme = this.rechercheUtilisateurs().trim().toLowerCase();
    const role = this.filtreRole();
    return this.utilisateurs().filter((u) => {
      const correspondRole = role === 'TOUS' || u.role === role;
      const correspondTerme =
        !terme ||
        `${u.prenom} ${u.nom}`.toLowerCase().includes(terme) ||
        u.email.toLowerCase().includes(terme);
      return correspondRole && correspondTerme;
    });
  });

  protected readonly coursFiltres = computed(() => {
    const terme = this.rechercheCours().trim().toLowerCase();
    const statut = this.filtreStatut();
    return this.cours().filter((c) => {
      const correspondStatut =
        statut === 'TOUS' || (statut === 'ACTIFS' && c.actif) || (statut === 'INACTIFS' && !c.actif);
      const correspondTerme =
        !terme || c.titre.toLowerCase().includes(terme) || c.enseignantNom.toLowerCase().includes(terme);
      return correspondStatut && correspondTerme;
    });
  });

  protected readonly utilisateursPage = computed(() => {
    const debut = (this.pageUtilisateurs() - 1) * TAILLE_PAGE;
    return this.utilisateursFiltres().slice(debut, debut + TAILLE_PAGE);
  });

  protected readonly coursPagine = computed(() => {
    const debut = (this.pageCours() - 1) * TAILLE_PAGE;
    return this.coursFiltres().slice(debut, debut + TAILLE_PAGE);
  });

  constructor() {
    this.utilisateurService.listerTous().subscribe((u) => this.utilisateurs.set(u));
    this.rafraichirCours();
  }

  private rafraichirCours(): void {
    this.coursService.listerTousLesCours().subscribe({
      next: (c) => {
        this.cours.set(c);
        this.chargement.set(false);
      },
      error: () => this.chargement.set(false)
    });
  }

  rechercherUtilisateurs(terme: string): void {
    this.rechercheUtilisateurs.set(terme);
    this.pageUtilisateurs.set(1);
  }

  filtrerRole(role: FiltreRole): void {
    this.filtreRole.set(role);
    this.pageUtilisateurs.set(1);
  }

  rechercherCours(terme: string): void {
    this.rechercheCours.set(terme);
    this.pageCours.set(1);
  }

  filtrerStatut(statut: FiltreStatut): void {
    this.filtreStatut.set(statut);
    this.pageCours.set(1);
  }

  basculerStatut(cours: Cours): void {
    if (this.statutEnCours() !== null) {
      return;
    }
    const administrateurId = this.auth.utilisateurConnecte()!.id;
    this.statutEnCours.set(cours.id);
    this.coursService.basculerStatut(cours.id, administrateurId).subscribe({
      next: () => {
        this.statutEnCours.set(null);
        this.rafraichirCours();
      },
      error: () => this.statutEnCours.set(null)
    });
  }
}
