import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RoleUtilisateur } from '../models/utilisateur.model';
import { AuthService } from '../services/auth.service';

// Garde minimale : une verification complete (Spring Security, JWT) est
// prevue en Phase 3 (cf demarage/analyse-besoins-conception.md). Ici on
// evite juste qu'un etudiant tombe sur une page d'espace enseignant/admin.
export function roleGuard(rolesAutorises: RoleUtilisateur[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const utilisateur = auth.utilisateurConnecte();

    if (!utilisateur) {
      return router.createUrlTree(['/connexion']);
    }
    if (!rolesAutorises.includes(utilisateur.role)) {
      return router.createUrlTree(['/cours']);
    }
    return true;
  };
}
