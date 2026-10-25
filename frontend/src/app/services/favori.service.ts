import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Cours } from '../models/cours.model';
import { BasculeReponse } from '../models/progression.model';
import { API_BASE_URL } from './api-config';

@Injectable({ providedIn: 'root' })
export class FavoriService {
  private readonly http = inject(HttpClient);

  basculer(etudiantId: string, coursId: string): Observable<BasculeReponse> {
    return this.http.post<BasculeReponse>(`${API_BASE_URL}/favoris`, { etudiantId, coursId });
  }

  listerFavoris(etudiantId: string): Observable<Cours[]> {
    return this.http.get<Cours[]>(`${API_BASE_URL}/favoris/etudiant/${etudiantId}`);
  }
}
