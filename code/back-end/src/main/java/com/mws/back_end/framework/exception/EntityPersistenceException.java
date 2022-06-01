package com.mws.back_end.framework.exception;


import javax.persistence.PersistenceException;

import org.hibernate.PersistentObjectException;
import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLException;
import java.util.Arrays;

public class EntityPersistenceException extends RuntimeException {
    final PersistenceExceptionType type;
    public enum PersistenceExceptionType {
        DUPLICATE_KEY
    }

    private EntityPersistenceException() {
        type = null;
    }

    public EntityPersistenceException(final String message) {
        super(message);
        type = null;
    }

    public EntityPersistenceException(final String message, final PersistenceExceptionType type) {
        super(message);
        this.type = type;
    }

    public static EntityPersistenceException toDatabaseException(final PersistenceException e) {
        if (isDuplicateEntry(e)) {
            return new EntityPersistenceException(toDuplicateEntryMessage(e), PersistenceExceptionType.DUPLICATE_KEY);
        }

        throw new RuntimeException(e);
    }

    private static boolean isDuplicateEntry(PersistenceException e) {
        if (e == null || !(e.getCause() instanceof ConstraintViolationException)) {
            return false;
        }
        PersistentObjectException te;
        final SQLException sqlException = ((ConstraintViolationException) e.getCause()).getSQLException();

        return 1062 == sqlException.getErrorCode() && "23000".equals(sqlException.getSQLState());
    }

    private static String toDuplicateEntryMessage(final PersistenceException e) {
        final String message = ((ConstraintViolationException) e.getCause()).getSQLException().getMessage();
        return Arrays.stream(message.split(" for key '")).findFirst().orElse("Duplicated entry.");
    }
}
