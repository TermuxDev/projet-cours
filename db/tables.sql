-- Chaque table expose deux identifiants :
--   id        INT IDENTITY -> cle primaire interne, utilisee pour les
--             jointures (rapide, jamais renvoyee au client).
--   publicId  UNIQUEIDENTIFIER -> identifiant public, le seul utilise
--             dans l'API et les URLs Angular (ex: /cours/3f2a...), pour
--             ne jamais exposer de sequence devinable (1, 2, 3...).

CREATE TABLE utilisateurs (
    id            INT IDENTITY(1,1) PRIMARY KEY,
    publicId      UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID() UNIQUE,
    nom           NVARCHAR(100)  NOT NULL,
    prenom        NVARCHAR(100)  NOT NULL,
    email         NVARCHAR(150)  NOT NULL UNIQUE,
    motDePasse    NVARCHAR(255)  NOT NULL,
    role          VARCHAR(20)    NOT NULL CHECK (role IN ('ETUDIANT','ENSEIGNANT','ADMINISTRATEUR')),
    dateCreation  DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
    -- Faux tant que le code recu par e-mail n'a pas ete valide. Les comptes
    -- de demo (DataSeeder) sont crees directement verifies.
    emailVerifie  BIT            NOT NULL DEFAULT 1
);

CREATE TABLE cours (
    id            INT IDENTITY(1,1) PRIMARY KEY,
    publicId      UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID() UNIQUE,
    titre         NVARCHAR(200)  NOT NULL,
    description   NVARCHAR(MAX)  NULL,
    categorie     NVARCHAR(100)  NULL,
    enseignantId  INT            NOT NULL REFERENCES utilisateurs(id),
    dateCreation  DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
    actif         BIT            NOT NULL DEFAULT 1
);

CREATE TABLE chapitres (
    id             INT IDENTITY(1,1) PRIMARY KEY,
    coursId        INT            NOT NULL REFERENCES cours(id) ON DELETE CASCADE,
    titre          NVARCHAR(200)  NOT NULL,
    ordre          INT            NOT NULL DEFAULT 1,
    -- Support de cours sous forme de lien (video, document externe...),
    -- pas d'upload de fichier en Phase 1/2.
    lienRessource  NVARCHAR(500)  NULL
);

CREATE TABLE inscriptions (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    publicId         UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID() UNIQUE,
    etudiantId       INT        NOT NULL REFERENCES utilisateurs(id),
    coursId          INT        NOT NULL REFERENCES cours(id),
    dateInscription  DATETIME2  NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT UQ_inscription UNIQUE (etudiantId, coursId)
);

-- Suivi de progression : un etudiant termine un chapitre.
CREATE TABLE progressions (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    etudiantId      INT        NOT NULL REFERENCES utilisateurs(id),
    chapitreId      INT        NOT NULL REFERENCES chapitres(id) ON DELETE CASCADE,
    dateCompletion  DATETIME2  NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT UQ_progression UNIQUE (etudiantId, chapitreId)
);

-- Cours mis en favori par un etudiant ("like").
CREATE TABLE favoris (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    etudiantId  INT        NOT NULL REFERENCES utilisateurs(id),
    coursId     INT        NOT NULL REFERENCES cours(id) ON DELETE CASCADE,
    dateAjout   DATETIME2  NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT UQ_favori UNIQUE (etudiantId, coursId)
);

-- Codes a 6 chiffres envoyes par e-mail : validation d'inscription et
-- reinitialisation de mot de passe partagent la meme table.
CREATE TABLE codes_verification (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    utilisateurId    INT          NOT NULL REFERENCES utilisateurs(id) ON DELETE CASCADE,
    code             VARCHAR(6)   NOT NULL,
    type             VARCHAR(20)  NOT NULL CHECK (type IN ('INSCRIPTION','REINITIALISATION')),
    dateExpiration   DATETIME2    NOT NULL,
    dateCreation     DATETIME2    NOT NULL DEFAULT SYSDATETIME(),
    utilise          BIT          NOT NULL DEFAULT 0
);
GO
