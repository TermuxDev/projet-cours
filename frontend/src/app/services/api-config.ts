// Utilise le meme hote que celui par lequel le frontend a ete ouvert
// (localhost, 192.168.137.164, ...) : l'IP n'est donc jamais figee en dur,
// l'appli fonctionne sur le reseau local sans configuration manuelle.
export const API_BASE_URL = `http://${window.location.hostname}:8080/api`;
