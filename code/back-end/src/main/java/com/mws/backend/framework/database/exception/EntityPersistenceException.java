package com.mws.backend.framework.database.exception;

import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLException;

public class EntityPersistenceException extends RuntimeException {
    private EntityPersistenceException() {}

    public EntityPersistenceException(final String message) {
        super(message);
    }

    public static EntityPersistenceException toDatabaseException(final PersistenceException e) {
        if (isDuplicateEntry(e)) {
           return new EntityPersistenceException(toDuplicateEntryMessage(e));
        }

        throw new RuntimeException(e.getMessage());
    }

    private static boolean isDuplicateEntry(PersistenceException e) {
        if (e == null || !(e.getCause() instanceof ConstraintViolationException)) {
            return false;
        }
        final SQLException sqlException = ((ConstraintViolationException) e.getCause()).getSQLException();

        return "23000".equals(sqlException.getSQLState()) && 1062 == sqlException.getErrorCode();
    }

    private static String toDuplicateEntryMessage(PersistenceException e) {
        return e.getMessage();
    }
}
