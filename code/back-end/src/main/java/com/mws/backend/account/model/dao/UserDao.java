package com.mws.backend.account.model.dao;

import com.mws.backend.account.model.entity.User;
import com.mws.backend.framework.database.GenericDaoImpl;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.mws.backend.account.model.entity.User.USER_COLUMN_EMAIL;

@Component
public class UserDao extends GenericDaoImpl<User, Long> {

    public Collection<User> findByEmail(final String email) {
        return findBy(USER_COLUMN_EMAIL, email, 1);
    }
}

