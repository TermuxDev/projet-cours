import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Inscription, InscriptionRequete } from '../models/inscription.model';
import { API_BASE_URL } from './api-config';

@Injectable({ providedIn: 'root' })
export class InscriptionService {
  private readonly http = inject(HttpClient);

  inscrire(requete: InscriptionRequete): Observable<Inscription> {
    return this.http.post<Inscription>(`${API_BASE_URL}/inscriptions`, requete);
  }

  listerPourEtudiant(etudiantId: string): Observable<Inscription[]> {
    return this.http.get<Inscription[]>(`${API_BASE_URL}/inscriptions/etudiant/${etudiantId}`);
  }
}
