import { Routes } from '@angular/router';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'cours', pathMatch: 'full' },
  {
    path: 'connexion',
    loadComponent: () => import('./pages/connexion/connexion').then((m) => m.Connexion)
  },
  {
    path: 'inscription',
    loadComponent: () => import('./pages/inscription/inscription').then((m) => m.Inscription)
  },
  {
    path: 'mot-de-passe-oublie',
    loadComponent: () =>
      import('./pages/mot-de-passe-oublie/mot-de-passe-oublie').then((m) => m.MotDePasseOublie)
  },
  {
    path: 'a-propos',
    loadComponent: () => import('./pages/a-propos/a-propos').then((m) => m.APropos)
  },
  {
    path: 'cours',
    loadComponent: () => import('./pages/catalogue-cours/catalogue-cours').then((m) => m.CatalogueCours)
  },
  {
    path: 'cours/:id',
    loadComponent: () => import('./pages/detail-cours/detail-cours').then((m) => m.DetailCours)
  },
  {
    path: 'cours/:coursId/chapitres/:chapitreId',
    loadComponent: () => import('./pages/chapitre-vue/chapitre-vue').then((m) => m.ChapitreVue)
  },
  {
    path: 'etudiant',
    canActivate: [roleGuard(['ETUDIANT'])],
    loadComponent: () =>
      import('./pages/tableau-bord-etudiant/tableau-bord-etudiant').then((m) => m.TableauBordEtudiant)
  },
  {
    path: 'enseignant',
    canActivate: [roleGuard(['ENSEIGNANT'])],
    loadComponent: () =>
      import('./pages/tableau-bord-enseignant/tableau-bord-enseignant').then((m) => m.TableauBordEnseignant)
  },
  {
    path: 'admin',
    canActivate: [roleGuard(['ADMINISTRATEUR'])],
    loadComponent: () => import('./pages/tableau-bord-admin/tableau-bord-admin').then((m) => m.TableauBordAdmin)
  },
  { path: '**', redirectTo: 'cours' }
];
