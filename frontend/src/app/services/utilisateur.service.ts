import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Utilisateur } from '../models/utilisateur.model';
import { API_BASE_URL } from './api-config';

@Injectable({ providedIn: 'root' })
export class UtilisateurService {
  private readonly http = inject(HttpClient);

  listerTous(): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(`${API_BASE_URL}/utilisateurs`);
  }

  obtenir(id: string): Observable<Utilisateur> {
    return this.http.get<Utilisateur>(`${API_BASE_URL}/utilisateurs/${id}`);
  }
}
