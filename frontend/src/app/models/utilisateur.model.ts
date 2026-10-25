export type RoleUtilisateur = 'ETUDIANT' | 'ENSEIGNANT' | 'ADMINISTRATEUR';

export interface Utilisateur {
  id: string;
  nom: string;
  prenom: string;
  email: string;
  role: RoleUtilisateur;
  dateCreation: string;
}

export interface ConnexionRequete {
  email: string;
  motDePasse: string;
}

export interface InscriptionRequete {
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  role: 'ETUDIANT' | 'ENSEIGNANT';
}

export interface OtpRequete {
  email: string;
  code: string;
}

export interface ReinitialisationRequete {
  email: string;
  code: string;
  nouveauMotDePasse: string;
}
