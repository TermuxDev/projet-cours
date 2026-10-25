package com.cours.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GestionnaireExceptionsGlobal {

    @ExceptionHandler(RessourceNonTrouveeException.class)
    public ResponseEntity<ErreurReponse> gererNonTrouvee(RessourceNonTrouveeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErreurReponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(InscriptionDejaExistanteException.class)
    public ResponseEntity<ErreurReponse> gererInscriptionExistante(InscriptionDejaExistanteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErreurReponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    @ExceptionHandler(IdentifiantsInvalidesException.class)
    public ResponseEntity<ErreurReponse> gererIdentifiantsInvalides(IdentifiantsInvalidesException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErreurReponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ExceptionHandler(AccesRefuseException.class)
    public ResponseEntity<ErreurReponse> gererAccesRefuse(AccesRefuseException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErreurReponse(HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }

    @ExceptionHandler(CompteNonVerifieException.class)
    public ResponseEntity<ErreurReponse> gererCompteNonVerifie(CompteNonVerifieException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErreurReponse(HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }

    @ExceptionHandler(EmailDejaUtiliseException.class)
    public ResponseEntity<ErreurReponse> gererEmailDejaUtilise(EmailDejaUtiliseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErreurReponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    @ExceptionHandler(CodeInvalideException.class)
    public ResponseEntity<ErreurReponse> gererCodeInvalide(CodeInvalideException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErreurReponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurReponse> gererValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(erreur -> erreur.getField() + " : " + erreur.getDefaultMessage())
                .orElse("Donnees invalides");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErreurReponse(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurReponse> gererErreurInattendue(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErreurReponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erreur interne : " + ex.getMessage()));
    }
}
