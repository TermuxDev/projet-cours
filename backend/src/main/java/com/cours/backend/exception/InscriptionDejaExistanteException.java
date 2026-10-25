package com.cours.backend.exception;

public class InscriptionDejaExistanteException extends RuntimeException {
    public InscriptionDejaExistanteException(String message) {
        super(message);
    }
}
