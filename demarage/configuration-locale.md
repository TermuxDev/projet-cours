# Configuration locale (identifiants)

Les vrais identifiants (mot de passe SQL Server, compte e-mail d'envoi des
codes de verification) ne sont pas dans `application.properties`
Ils vivent dans :

```
backend/src/main/resources/application-local.properties
```

Ce fichier est volontairement exclu du suivi de version (`.gitignore`).
Sur une nouvelle machine, recreer ce fichier a partir de ce modele :

```properties
DB_USERNAME=sa
DB_PASSWORD=...
MAIL_USERNAME=...
MAIL_APP_PASSWORD=...
```
