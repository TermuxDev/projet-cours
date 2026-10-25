import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { BasculeReponse, ProgressionCours } from '../models/progression.model';
import { API_BASE_URL } from './api-config';

@Injectable({ providedIn: 'root' })
export class ProgressionService {
  private readonly http = inject(HttpClient);

  basculer(etudiantId: string, chapitreId: number): Observable<BasculeReponse> {
    return this.http.post<BasculeReponse>(`${API_BASE_URL}/progressions`, { etudiantId, chapitreId });
  }

  obtenirProgression(etudiantId: string, coursId: string): Observable<ProgressionCours> {
    return this.http.get<ProgressionCours>(`${API_BASE_URL}/progressions/etudiant/${etudiantId}/cours/${coursId}`);
  }
}
