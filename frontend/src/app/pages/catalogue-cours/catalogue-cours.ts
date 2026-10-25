import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CoursService } from '../../services/cours.service';
import { Cours } from '../../models/cours.model';
import { CarteCours } from '../../components/carte-cours/carte-cours';
import { Mascotte } from '../../components/mascotte/mascotte';
import { Pagination } from '../../components/pagination/pagination';

const TAILLE_PAGE = 9;

@Component({
  selector: 'app-catalogue-cours',
  standalone: true,
  imports: [FormsModule, CarteCours, Mascotte, Pagination],
  templateUrl: './catalogue-cours.html',
  styleUrl: './catalogue-cours.scss'
})
export class CatalogueCours {
  private readonly coursService = inject(CoursService);

  protected readonly coursListe = signal<Cours[]>([]);
  protected readonly chargement = signal(true);
  protected readonly erreur = signal<string | null>(null);
  protected readonly recherche = signal('');
  protected readonly pageActuelle = signal(1);
  protected readonly taillePage = TAILLE_PAGE;

  protected readonly coursFiltres = computed(() => {
    const terme = this.recherche().trim().toLowerCase();
    if (!terme) {
      return this.coursListe();
    }
    return this.coursListe().filter((cours) =>
      cours.titre.toLowerCase().includes(terme) || cours.categorie?.toLowerCase().includes(terme)
    );
  });

  protected readonly coursPage = computed(() => {
    const debut = (this.pageActuelle() - 1) * TAILLE_PAGE;
    return this.coursFiltres().slice(debut, debut + TAILLE_PAGE);
  });

  constructor() {
    this.coursService.listerCoursActifs().subscribe({
      next: (cours) => {
        this.coursListe.set(cours);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set("Oups, impossible de charger les cours pour le moment. Reessaie dans un instant.");
        this.chargement.set(false);
      }
    });
  }

  rechercher(terme: string): void {
    this.recherche.set(terme);
    this.pageActuelle.set(1);
  }
}
