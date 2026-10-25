export interface Inscription {
  id: string;
  etudiantId: string;
  coursId: string;
  coursTitre: string;
  nombreChapitres: number;
  pourcentageProgression: number;
  dateInscription: string;
}

export interface InscriptionRequete {
  etudiantId: string;
  coursId: string;
}
