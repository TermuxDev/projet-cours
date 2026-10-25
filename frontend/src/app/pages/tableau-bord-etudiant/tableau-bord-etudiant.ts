import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { InscriptionService } from '../../services/inscription.service';
import { FavoriService } from '../../services/favori.service';
import { Inscription } from '../../models/inscription.model';
import { Cours } from '../../models/cours.model';
import { CarteCours } from '../../components/carte-cours/carte-cours';

@Component({
  selector: 'app-tableau-bord-etudiant',
  standalone: true,
  imports: [CarteCours, RouterLink],
  templateUrl: './tableau-bord-etudiant.html',
  styleUrl: './tableau-bord-etudiant.scss'
})
export class TableauBordEtudiant {
  protected readonly auth = inject(AuthService);
  private readonly inscriptionService = inject(InscriptionService);
  private readonly favoriService = inject(FavoriService);

  protected readonly inscriptions = signal<Inscription[]>([]);
  protected readonly favoris = signal<Cours[]>([]);
  protected readonly chargement = signal(true);

  protected readonly nombreTermines = computed(
    () => this.inscriptions().filter((i) => i.pourcentageProgression === 100).length
  );

  constructor() {
    const utilisateur = this.auth.utilisateurConnecte()!;

    this.inscriptionService.listerPourEtudiant(utilisateur.id).subscribe({
      next: (inscriptions) => {
        this.inscriptions.set(inscriptions);
        this.chargement.set(false);
      },
      error: () => this.chargement.set(false)
    });

    this.favoriService.listerFavoris(utilisateur.id).subscribe((favoris) => this.favoris.set(favoris));
  }
}
