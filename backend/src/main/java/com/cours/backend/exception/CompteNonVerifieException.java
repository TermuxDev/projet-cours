package com.cours.backend.exception;

public class CompteNonVerifieException extends RuntimeException {
    public CompteNonVerifieException(String message) {
        super(message);
    }
}
