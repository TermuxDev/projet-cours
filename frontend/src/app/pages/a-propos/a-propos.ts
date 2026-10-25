import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CoursService } from '../../services/cours.service';
import { UtilisateurService } from '../../services/utilisateur.service';
import { AuthService } from '../../services/auth.service';
import { Mascotte } from '../../components/mascotte/mascotte';

interface Question {
  titre: string;
  reponse: string;
}

@Component({
  selector: 'app-a-propos',
  standalone: true,
  imports: [RouterLink, Mascotte],
  templateUrl: './a-propos.html',
  styleUrl: './a-propos.scss'
})
export class APropos {
  protected readonly auth = inject(AuthService);
  private readonly coursService = inject(CoursService);
  private readonly utilisateurService = inject(UtilisateurService);

  protected readonly nombreCours = signal(0);
  protected readonly nombreEtudiants = signal(0);
  protected readonly nombreEnseignants = signal(0);

  protected readonly questionOuverte = signal<number | null>(0);

  protected readonly questions: Question[] = [
    {
      titre: "C'est gratuit ?",
      reponse: "Oui. Cours+ est un projet academique : la creation de compte, l'inscription aux cours et le suivi de progression sont libres d'acces."
    },
    {
      titre: 'Qui peut publier un cours ?',
      reponse: "Tout compte enseignant. Apres inscription, tu peux creer un cours, y ajouter des chapitres et y attacher des ressources (lien video, article, document externe)."
    },
    {
      titre: 'Comment fonctionne la progression ?',
      reponse: "Chaque chapitre termine coche une case. Ton pourcentage d'avancement par cours se recalcule automatiquement, visible sur ton tableau de bord etudiant."
    },
    {
      titre: 'Mes donnees sont-elles en securite ?',
      reponse: "Les mots de passe sont hashes (BCrypt), jamais stockes en clair, et l'inscription est validee par un code envoye par e-mail avant toute connexion possible."
    }
  ];

  protected readonly estConnecte = computed(() => this.auth.estConnecte());

  constructor() {
    this.coursService.listerCoursActifs().subscribe((cours) => this.nombreCours.set(cours.length));
    this.utilisateurService.listerTous().subscribe((utilisateurs) => {
      this.nombreEtudiants.set(utilisateurs.filter((u) => u.role === 'ETUDIANT').length);
      this.nombreEnseignants.set(utilisateurs.filter((u) => u.role === 'ENSEIGNANT').length);
    });
  }

  basculerQuestion(index: number): void {
    this.questionOuverte.set(this.questionOuverte() === index ? null : index);
  }
}
