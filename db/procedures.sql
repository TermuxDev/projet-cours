-- ------------------------------------------------------------
-- FONCTION : nombre d'etudiants inscrits a un cours
-- ------------------------------------------------------------
CREATE OR ALTER FUNCTION fn_NombreInscrits (@coursId INT)
RETURNS INT
AS
BEGIN
    DECLARE @total INT;
    SELECT @total = COUNT(*) FROM inscriptions WHERE coursId = @coursId;
    RETURN @total;
END
GO

-- ------------------------------------------------------------
-- PROCEDURE : inscription d'un etudiant a un cours
-- Centralise la regle "un etudiant ne peut s'inscrire deux fois
-- au meme cours" et "le cours doit etre actif" au niveau base.
-- ------------------------------------------------------------
CREATE OR ALTER PROCEDURE sp_InscrireEtudiant
    @etudiantId INT,
    @coursId    INT
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT EXISTS (SELECT 1 FROM cours WHERE id = @coursId AND actif = 1)
    BEGIN
        RAISERROR('Cours introuvable ou inactif', 16, 1);
        RETURN;
    END

    IF EXISTS (SELECT 1 FROM inscriptions WHERE etudiantId = @etudiantId AND coursId = @coursId)
    BEGIN
        RAISERROR('Etudiant deja inscrit a ce cours', 16, 1);
        RETURN;
    END

    INSERT INTO inscriptions (etudiantId, coursId) VALUES (@etudiantId, @coursId);
END
GO

-- ------------------------------------------------------------
-- FONCTION : pourcentage de progression d'un etudiant sur un cours
-- ------------------------------------------------------------
CREATE OR ALTER FUNCTION fn_PourcentageProgression (@etudiantId INT, @coursId INT)
RETURNS INT
AS
BEGIN
    DECLARE @total INT, @complete INT;

    SELECT @total = COUNT(*) FROM chapitres WHERE coursId = @coursId;
    IF @total IS NULL OR @total = 0
        RETURN 0;

    SELECT @complete = COUNT(*)
    FROM progressions p
    JOIN chapitres c ON c.id = p.chapitreId
    WHERE p.etudiantId = @etudiantId AND c.coursId = @coursId;

    RETURN (@complete * 100) / @total;
END
GO

-- ------------------------------------------------------------
-- PROCEDURE : marquer un chapitre comme termine
-- Centralise la regle "il faut etre inscrit au cours pour valider
-- sa progression" au niveau base.
-- ------------------------------------------------------------
CREATE OR ALTER PROCEDURE sp_MarquerChapitreComplete
    @etudiantId INT,
    @chapitreId INT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @coursId INT;

    SELECT @coursId = coursId FROM chapitres WHERE id = @chapitreId;
    IF @coursId IS NULL
    BEGIN
        RAISERROR('Chapitre introuvable', 16, 1);
        RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM inscriptions WHERE etudiantId = @etudiantId AND coursId = @coursId)
    BEGIN
        RAISERROR('Etudiant non inscrit a ce cours', 16, 1);
        RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM progressions WHERE etudiantId = @etudiantId AND chapitreId = @chapitreId)
    BEGIN
        INSERT INTO progressions (etudiantId, chapitreId) VALUES (@etudiantId, @chapitreId);
    END
END
GO
