import { Component, computed, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { CoursService } from '../../services/cours.service';
import { InscriptionService } from '../../services/inscription.service';
import { ProgressionService } from '../../services/progression.service';
import { AuthService } from '../../services/auth.service';
import { CoursDetail } from '../../models/cours.model';
import { ProgressionCours } from '../../models/progression.model';

// Oeuvres hebergees sur ces plateformes : on peut les afficher directement
// dans la page (lecteur video integre) plutot qu'un simple lien sortant.
function urlIncorporee(lien: string): string | null {
  const youtube = lien.match(/(?:youtube\.com\/watch\?v=|youtu\.be\/)([\w-]{11})/);
  if (youtube) {
    return `https://www.youtube.com/embed/${youtube[1]}`;
  }
  const vimeo = lien.match(/vimeo\.com\/(\d+)/);
  if (vimeo) {
    return `https://player.vimeo.com/video/${vimeo[1]}`;
  }
  return null;
}

@Component({
  selector: 'app-chapitre-vue',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './chapitre-vue.html',
  styleUrl: './chapitre-vue.scss'
})
export class ChapitreVue {
  private readonly route = inject(ActivatedRoute);
  private readonly coursService = inject(CoursService);
  private readonly inscriptionService = inject(InscriptionService);
  private readonly progressionService = inject(ProgressionService);
  private readonly sanitizer = inject(DomSanitizer);
  protected readonly auth = inject(AuthService);

  // Reactifs aux changements de parametres de route : Angular reutilise
  // l'instance du composant quand on navigue entre deux URLs de la meme
  // route (ex. chapitre precedent/suivant), donc un simple snapshot lu
  // une fois au demarrage ne se mettait jamais a jour (bug des boutons
  // precedent/suivant qui semblaient ne "rien faire").
  protected readonly coursId = toSignal(
    this.route.paramMap.pipe(map((p) => p.get('coursId')!)),
    { requireSync: true }
  );
  protected readonly chapitreId = toSignal(
    this.route.paramMap.pipe(map((p) => Number(p.get('chapitreId')))),
    { requireSync: true }
  );

  protected readonly cours = signal<CoursDetail | null>(null);
  protected readonly chargement = signal(true);
  protected readonly erreur = signal<string | null>(null);
  protected readonly estInscrit = signal(false);
  protected readonly progression = signal<ProgressionCours>({ pourcentage: 0, chapitresTerminesIds: [] });
  protected readonly basculeEnCours = signal(false);

  protected readonly estProprietaire = computed(() => {
    const utilisateur = this.auth.utilisateurConnecte();
    const cours = this.cours();
    return !!utilisateur && !!cours && utilisateur.role === 'ENSEIGNANT' && utilisateur.id === cours.enseignantId;
  });

  protected readonly aAcces = computed(() => this.estInscrit() || this.estProprietaire());

  protected readonly chapitre = computed(
    () => this.cours()?.chapitres.find((c) => c.id === this.chapitreId()) ?? null
  );

  protected readonly index = computed(() => {
    const cours = this.cours();
    if (!cours) return -1;
    return cours.chapitres.findIndex((c) => c.id === this.chapitreId());
  });

  protected readonly precedent = computed(() => {
    const cours = this.cours();
    const i = this.index();
    return cours && i > 0 ? cours.chapitres[i - 1] : null;
  });

  protected readonly suivant = computed(() => {
    const cours = this.cours();
    const i = this.index();
    return cours && i >= 0 && i < cours.chapitres.length - 1 ? cours.chapitres[i + 1] : null;
  });

  protected readonly estTermine = computed(() => this.progression().chapitresTerminesIds.includes(this.chapitreId()));

  protected readonly urlIncorporeeSure = computed<SafeResourceUrl | null>(() => {
    const lien = this.chapitre()?.lienRessource;
    if (!lien) return null;
    const url = urlIncorporee(lien);
    return url ? this.sanitizer.bypassSecurityTrustResourceUrl(url) : null;
  });

  constructor() {
    // Ne recharge le cours que si on change reellement de cours (pas a
    // chaque chapitre) : naviguer entre chapitres reste donc instantane.
    effect(() => {
      this.chargerCours(this.coursId());
    });
  }

  private chargerCours(coursId: string): void {
    this.chargement.set(true);
    this.erreur.set(null);
    const visiteurId = this.auth.utilisateurConnecte()?.id;

    this.coursService.obtenirDetail(coursId, visiteurId).subscribe({
      next: (cours) => {
        this.cours.set(cours);
        this.chargement.set(false);
        this.chargerEtatEtudiant(coursId);
      },
      error: () => {
        this.erreur.set('Ce cours est introuvable ou a ete depublie.');
        this.chargement.set(false);
      }
    });
  }

  private chargerEtatEtudiant(coursId: string): void {
    const utilisateur = this.auth.utilisateurConnecte();
    if (!utilisateur || utilisateur.role !== 'ETUDIANT') {
      return;
    }

    this.inscriptionService.listerPourEtudiant(utilisateur.id).subscribe((inscriptions) => {
      const inscrit = inscriptions.some((i) => i.coursId === coursId);
      this.estInscrit.set(inscrit);
      if (inscrit) {
        this.progressionService.obtenirProgression(utilisateur.id, coursId).subscribe((p) => this.progression.set(p));
      }
    });
  }

  basculerTermine(): void {
    const utilisateur = this.auth.utilisateurConnecte();
    const cours = this.cours();
    if (!utilisateur || !cours || this.basculeEnCours()) {
      return;
    }

    this.basculeEnCours.set(true);
    this.progressionService.basculer(utilisateur.id, this.chapitreId()).subscribe({
      next: () => {
        this.basculeEnCours.set(false);
        this.progressionService.obtenirProgression(utilisateur.id, cours.id).subscribe((p) => this.progression.set(p));
      },
      error: () => this.basculeEnCours.set(false)
    });
  }
}
