import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Mascotte } from '../../components/mascotte/mascotte';

@Component({
  selector: 'app-connexion',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, Mascotte],
  templateUrl: './connexion.html',
  styleUrl: './connexion.scss'
})
export class Connexion {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly enCours = signal(false);
  protected readonly erreur = signal<string | null>(null);
  protected readonly motDePasseVisible = signal(false);

  protected readonly formulaire = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    motDePasse: ['', Validators.required]
  });

  soumettre(): void {
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }

    this.enCours.set(true);
    this.erreur.set(null);

    this.auth.connexion(this.formulaire.getRawValue()).subscribe({
      next: () => this.router.navigateByUrl('/cours'),
      error: (reponse: HttpErrorResponse) => {
        this.enCours.set(false);
        this.erreur.set(reponse.error?.message ?? 'Connexion impossible pour le moment.');
      }
    });
  }
}
