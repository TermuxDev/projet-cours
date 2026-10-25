import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Mascotte } from '../../components/mascotte/mascotte';

type Etape = 'email' | 'reinitialisation';

@Component({
  selector: 'app-mot-de-passe-oublie',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, Mascotte],
  templateUrl: './mot-de-passe-oublie.html',
  styleUrl: './mot-de-passe-oublie.scss'
})
export class MotDePasseOublie {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly etape = signal<Etape>('email');
  protected readonly enCours = signal(false);
  protected readonly erreur = signal<string | null>(null);
  protected readonly motDePasseVisible = signal(false);

  protected readonly formulaireEmail = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]]
  });

  protected readonly formulaireReinitialisation = this.fb.nonNullable.group({
    code: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
    nouveauMotDePasse: ['', [Validators.required, Validators.minLength(6)]]
  });

  soumettreEmail(): void {
    if (this.formulaireEmail.invalid) {
      this.formulaireEmail.markAllAsTouched();
      return;
    }

    this.enCours.set(true);
    this.auth.demanderReinitialisation(this.formulaireEmail.controls.email.value).subscribe({
      next: () => {
        this.enCours.set(false);
        this.etape.set('reinitialisation');
      },
      error: () => {
        // Message volontairement identique en cas d'echec : on ne revele
        // jamais si un e-mail existe ou non.
        this.enCours.set(false);
        this.etape.set('reinitialisation');
      }
    });
  }

  soumettreReinitialisation(): void {
    if (this.formulaireReinitialisation.invalid) {
      this.formulaireReinitialisation.markAllAsTouched();
      return;
    }

    this.enCours.set(true);
    this.erreur.set(null);
    const { code, nouveauMotDePasse } = this.formulaireReinitialisation.getRawValue();
    const email = this.formulaireEmail.controls.email.value;

    this.auth.reinitialiserMotDePasse({ email, code, nouveauMotDePasse }).subscribe({
      next: () => {
        this.enCours.set(false);
        this.router.navigateByUrl('/connexion');
      },
      error: (reponse: HttpErrorResponse) => {
        this.enCours.set(false);
        this.erreur.set(reponse.error?.message ?? 'Code invalide.');
      }
    });
  }
}
