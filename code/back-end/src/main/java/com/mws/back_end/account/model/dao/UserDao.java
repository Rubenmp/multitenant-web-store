package com.mws.back_end.account.model.dao;

import com.mws.back_end.account.model.entity.User;
import com.mws.back_end.framework.database.GenericDaoImpl;
import org.springframework.stereotype.Component;

import static com.mws.back_end.account.model.entity.User.USER_COLUMN_EMAIL;

@Component
public class UserDao extends GenericDaoImpl<User, Long> {

    public User findByEmail(final String email) {
        return findBy(USER_COLUMN_EMAIL, email, 1).stream().findFirst().orElse(null);
    }
}

