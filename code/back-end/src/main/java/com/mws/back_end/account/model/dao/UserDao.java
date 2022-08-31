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
import static com.mws.back_end.framework.utils.ExceptionUtils.requireNotNull;

@Component
public class UserDao extends GenericDaoImpl<User, Long> {

    public User findByEmail(final String email) {
        return findBy(USER_COLUMN_EMAIL, email, 1).stream().findFirst().orElse(null);
    }

    public User findWithoutTenantFilter(final Long userId) {
        final CriteriaQuery<User> criteriaQuery = getCriteriaBuilder().createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Predicate predicate = getCriteriaBuilder().equal(root.get(DB_COLUMN_ID), userId);

        criteriaQuery.where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList().stream().findFirst().orElse(null);
    }

    public User findByEmail(final String email, final Long tenantId) {
        requireNotNull(email, "Email is required to handle user.");
        requireNotNull(tenantId, "Tenant is required to handle user.");
        final CriteriaQuery<User> criteriaQuery = getCriteriaBuilder().createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);

        Predicate predicate = getCriteriaBuilder().conjunction();
        predicate = getCriteriaBuilder().and(predicate, getCriteriaBuilder().equal(root.get(DB_COLUMN_TENANT_ID), tenantId));
        predicate = getCriteriaBuilder().and(predicate, getCriteriaBuilder().equal(root.get(USER_COLUMN_EMAIL), email));
        criteriaQuery.where(predicate);

        return entityManager.createQuery(criteriaQuery).setMaxResults(1).getResultList().stream().findFirst().orElse(null);
    }

    public List<User> fetchAllAdmins() {
        final CriteriaQuery<User> criteriaQuery = getCriteriaBuilder().createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);

        Predicate predicate = getCriteriaBuilder().equal(root.get(USER_COLUMN_ROLE), com.mws.back_end.account.model.entity.UserRole.ADMIN);
        criteriaQuery.where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}

