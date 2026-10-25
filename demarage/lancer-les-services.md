# Démarrer la plateforme en local

## 1. Base de données (Pour mon cas c'est Azure Data Studio car je suis sous Linux Ubuntu)

1. Se connecter à l'instance SQL Server locale.
2. Créer la base si elle n'existe pas : `CREATE DATABASE databasecours;`
3. Exécuter **dans cet ordre** :
   - `db/tables.sql`
   - `db/procedures.sql`


## 2. Backend (Spring Boot, port 8080)

```bash
cd backend
./mvnw spring-boot:run
```

Au premier démarrage, les comptes et cours de démo sont créés
automatiquement (voir `utilisateurs-demo.md`). 
L'API est disponible sur `http://localhost:8080/api`.

## 3. Frontend (Angular, port 4200)

```bash
cd frontend
npm start
```

Ouvrir `http://localhost:4200`. La page d'accueil redirige vers le
catalogue des cours.

## Accès depuis le réseau local

Le backend écoute sur toutes les interfaces (`server.address=0.0.0.0`)
