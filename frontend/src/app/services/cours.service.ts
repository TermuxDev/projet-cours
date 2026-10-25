import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Chapitre } from '../models/chapitre.model';
import {
  ChapitreCreationRequete,
  Cours,
  CoursCreationRequete,
  CoursDetail,
  CoursModificationRequete,
  Participant
} from '../models/cours.model';
import { BasculeReponse } from '../models/progression.model';
import { API_BASE_URL } from './api-config';

@Injectable({ providedIn: 'root' })
export class CoursService {
  private readonly http = inject(HttpClient);

  listerCoursActifs(): Observable<Cours[]> {
    return this.http.get<Cours[]>(`${API_BASE_URL}/cours`);
  }

  listerTousLesCours(): Observable<Cours[]> {
    return this.http.get<Cours[]>(`${API_BASE_URL}/cours/tous`);
  }

  listerCoursParEnseignant(enseignantId: string): Observable<Cours[]> {
    return this.http.get<Cours[]>(`${API_BASE_URL}/cours/enseignant/${enseignantId}`);
  }

  // visiteurId est optionnel : transmis quand un utilisateur est connecte,
  // pour que le backend decide s'il a acces aux liens de ressource
  // (inscrit, enseignant proprietaire ou administrateur uniquement).
  obtenirDetail(id: string, visiteurId?: string): Observable<CoursDetail> {
    const params: Record<string, string> = {};
    if (visiteurId) {
      params['visiteurId'] = visiteurId;
    }
    return this.http.get<CoursDetail>(`${API_BASE_URL}/cours/${id}`, { params });
  }

  listerParticipants(id: string): Observable<Participant[]> {
    return this.http.get<Participant[]>(`${API_BASE_URL}/cours/${id}/participants`);
  }

  creerCours(requete: CoursCreationRequete): Observable<void> {
    return this.http.post<void>(`${API_BASE_URL}/cours`, requete);
  }

  modifierCours(id: string, requete: CoursModificationRequete): Observable<void> {
    return this.http.put<void>(`${API_BASE_URL}/cours/${id}`, requete);
  }

  basculerStatut(id: string, utilisateurId: string): Observable<BasculeReponse> {
    return this.http.patch<BasculeReponse>(`${API_BASE_URL}/cours/${id}/statut`, { utilisateurId });
  }

  ajouterChapitre(coursId: string, requete: ChapitreCreationRequete): Observable<Chapitre> {
    return this.http.post<Chapitre>(`${API_BASE_URL}/cours/${coursId}/chapitres`, requete);
  }

  supprimerChapitre(coursId: string, chapitreId: number, enseignantId: string): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/cours/${coursId}/chapitres/${chapitreId}`, {
      params: { enseignantId }
    });
  }
}
