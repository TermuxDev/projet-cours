import { Chapitre } from './chapitre.model';

export interface Cours {
  id: string;
  titre: string;
  description: string;
  categorie: string;
  enseignantNom: string;
  nombreInscrits: number;
  nombreChapitres: number;
  actif: boolean;
}

export interface CoursCreationRequete {
  enseignantId: string;
  titre: string;
  description: string;
  categorie: string;
}

export interface CoursModificationRequete {
  enseignantId: string;
  titre: string;
  description: string;
  categorie: string;
}

export interface ChapitreCreationRequete {
  titre: string;
  lienRessource: string;
}

export interface CoursDetail {
  id: string;
  titre: string;
  description: string;
  categorie: string;
  enseignantId: string;
  enseignantNom: string;
  actif: boolean;
  nombreInscrits: number;
  chapitres: Chapitre[];
}

export interface Participant {
  nomAffiche: string;
  coursTermine: boolean;
}
