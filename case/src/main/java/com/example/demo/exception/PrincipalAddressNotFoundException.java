package com.example.demo.exception;

public class PrincipalAddressNotFoundException  extends RuntimeException{
    public PrincipalAddressNotFoundException(String message) {
        super(message);
    }
}
