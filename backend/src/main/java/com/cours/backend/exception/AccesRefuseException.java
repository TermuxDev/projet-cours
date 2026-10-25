package com.cours.backend.exception;

public class AccesRefuseException extends RuntimeException {
    public AccesRefuseException(String message) {
        super(message);
    }
}
