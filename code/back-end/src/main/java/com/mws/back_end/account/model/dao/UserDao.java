package com.mws.back_end.account.model.dao;

import com.mws.back_end.account.model.entity.User;
import com.mws.back_end.framework.database.GenericDaoImpl;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

import static com.mws.back_end.account.model.entity.User.USER_COLUMN_EMAIL;
import static com.mws.back_end.account.model.entity.User.USER_COLUMN_ROLE;

@Component
public class UserDao extends GenericDaoImpl<User, Long> {

    public User findByEmail(final String email) {
        return findBy(USER_COLUMN_EMAIL, email, 1).stream().findFirst().orElse(null);
    }

    public List<User> fetchAllAdmins() {
        final CriteriaQuery<User> criteriaQuery = getCriteriaBuilder().createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);

        Predicate predicate = getCriteriaBuilder().equal(root.get(USER_COLUMN_ROLE), com.mws.back_end.account.model.entity.UserRole.ADMIN);
        criteriaQuery.where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}

