package com.devsuperior.dscatalog.services.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class DataBaseException extends RuntimeException {

    public DataBaseException(String message) {
        super(message);
    }
}
