import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Mascotte } from '../../components/mascotte/mascotte';
import { RoleUtilisateur } from '../../models/utilisateur.model';

type Etape = 'details' | 'otp';

@Component({
  selector: 'app-inscription',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, Mascotte],
  templateUrl: './inscription.html',
  styleUrl: './inscription.scss'
})
export class Inscription {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly etape = signal<Etape>('details');
  protected readonly enCours = signal(false);
  protected readonly erreur = signal<string | null>(null);
  protected readonly motDePasseVisible = signal(false);
  protected readonly renvoiEnCours = signal(false);
  protected readonly renvoiConfirme = signal(false);

  protected readonly formulaire = this.fb.nonNullable.group({
    prenom: ['', Validators.required],
    nom: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    motDePasse: ['', [Validators.required, Validators.minLength(6)]],
    role: ['ETUDIANT' as RoleUtilisateur, Validators.required]
  });

  protected readonly formulaireOtp = this.fb.nonNullable.group({
    code: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]]
  });

  soumettreDetails(): void {
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }

    this.enCours.set(true);
    this.erreur.set(null);
    const valeurs = this.formulaire.getRawValue();

    this.auth.inscrire({ ...valeurs, role: valeurs.role as 'ETUDIANT' | 'ENSEIGNANT' }).subscribe({
      next: () => {
        this.enCours.set(false);
        this.etape.set('otp');
      },
      error: (reponse: HttpErrorResponse) => {
        this.enCours.set(false);
        this.erreur.set(reponse.error?.message ?? "L'inscription a echoue.");
      }
    });
  }

  soumettreOtp(): void {
    if (this.formulaireOtp.invalid) {
      this.formulaireOtp.markAllAsTouched();
      return;
    }

    this.enCours.set(true);
    this.erreur.set(null);
    const email = this.formulaire.controls.email.value;
    const code = this.formulaireOtp.controls.code.value;

    this.auth.verifierOtp({ email, code }).subscribe({
      next: (utilisateur) => {
        this.enCours.set(false);
        const accueil = utilisateur.role === 'ENSEIGNANT' ? '/enseignant' : '/etudiant';
        this.router.navigateByUrl(accueil);
      },
      error: (reponse: HttpErrorResponse) => {
        this.enCours.set(false);
        this.erreur.set(reponse.error?.message ?? 'Code invalide.');
      }
    });
  }

  renvoyerCode(): void {
    if (this.renvoiEnCours()) {
      return;
    }
    this.renvoiEnCours.set(true);
    this.renvoiConfirme.set(false);
    this.auth.renvoyerOtp(this.formulaire.controls.email.value).subscribe({
      next: () => {
        this.renvoiEnCours.set(false);
        this.renvoiConfirme.set(true);
      },
      error: () => this.renvoiEnCours.set(false)
    });
  }
}
