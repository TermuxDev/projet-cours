import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import {
  ConnexionRequete,
  InscriptionRequete,
  OtpRequete,
  ReinitialisationRequete,
  Utilisateur
} from '../models/utilisateur.model';
import { API_BASE_URL } from './api-config';
import { UtilisateurService } from './utilisateur.service';

const CLE_STOCKAGE = 'cours.session';
const DUREE_SESSION_MS = 12 * 60 * 60 * 1000; // 12h : simple expiration cote client.

interface SessionStockee {
  utilisateur: Utilisateur;
  expiration: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly utilisateurService = inject(UtilisateurService);
  private readonly router = inject(Router);

  private readonly utilisateurSignal = signal<Utilisateur | null>(this.lireDepuisStockage());

  readonly utilisateurConnecte = this.utilisateurSignal.asReadonly();
  readonly estConnecte = computed(() => this.utilisateurSignal() !== null);

  constructor() {
    // Revalide la session en tache de fond : si le compte n'existe plus
    // (ou que le stockage local a ete modifie a la main), on deconnecte.
    // Ceci reste une verification de coherence, pas une authentification
    // securisee : Spring Security / JWT est prevu en Phase 3.
    const utilisateur = this.utilisateurSignal();
    if (utilisateur) {
      this.utilisateurService.obtenir(utilisateur.id).subscribe({
        next: (actuel) => this.definirUtilisateur(actuel),
        error: () => this.deconnexion()
      });
    }
  }

  connexion(requete: ConnexionRequete): Observable<Utilisateur> {
    return this.http.post<Utilisateur>(`${API_BASE_URL}/auth/connexion`, requete).pipe(
      tap((utilisateur) => this.definirUtilisateur(utilisateur))
    );
  }

  inscrire(requete: InscriptionRequete): Observable<void> {
    return this.http.post<void>(`${API_BASE_URL}/auth/inscription`, requete);
  }

  verifierOtp(requete: OtpRequete): Observable<Utilisateur> {
    return this.http.post<Utilisateur>(`${API_BASE_URL}/auth/verifier-otp`, requete).pipe(
      tap((utilisateur) => this.definirUtilisateur(utilisateur))
    );
  }

  renvoyerOtp(email: string): Observable<void> {
    return this.http.post<void>(`${API_BASE_URL}/auth/renvoyer-otp`, { email });
  }

  demanderReinitialisation(email: string): Observable<void> {
    return this.http.post<void>(`${API_BASE_URL}/auth/mot-de-passe-oublie`, { email });
  }

  reinitialiserMotDePasse(requete: ReinitialisationRequete): Observable<void> {
    return this.http.post<void>(`${API_BASE_URL}/auth/reinitialiser-mot-de-passe`, requete);
  }

  deconnexion(): void {
    this.utilisateurSignal.set(null);
    localStorage.removeItem(CLE_STOCKAGE);
    // Sans ca, une page reservee a un role (ex. /enseignant) reste affichee
    // apres la deconnexion : les guards ne se re-declenchent que sur
    // navigation, pas quand l'etat d'auth change pendant qu'on est dessus.
    this.router.navigateByUrl('/cours');
  }

  private definirUtilisateur(utilisateur: Utilisateur): void {
    this.utilisateurSignal.set(utilisateur);
    const session: SessionStockee = { utilisateur, expiration: Date.now() + DUREE_SESSION_MS };
    localStorage.setItem(CLE_STOCKAGE, JSON.stringify(session));
  }

  private lireDepuisStockage(): Utilisateur | null {
    const brut = localStorage.getItem(CLE_STOCKAGE);
    if (!brut) {
      return null;
    }
    try {
      const session = JSON.parse(brut) as SessionStockee;
      if (!session.utilisateur || session.expiration < Date.now()) {
        localStorage.removeItem(CLE_STOCKAGE);
        return null;
      }
      return session.utilisateur;
    } catch {
      localStorage.removeItem(CLE_STOCKAGE);
      return null;
    }
  }
}
