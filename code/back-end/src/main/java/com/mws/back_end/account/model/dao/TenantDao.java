package com.mws.back_end.account.model.dao;

import com.mws.back_end.account.model.entity.Tenant;
import com.mws.back_end.framework.database.GenericDaoImpl;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

import static com.mws.back_end.account.model.entity.Tenant.TENANT_COLUMN_NAME;

@Component
public class TenantDao extends GenericDaoImpl<Tenant, Long> {
    // WARNING: generic dao methods contain extra tenantId security
    // Any method finding tenants in database must be overridden otherwise
    // the jwt token tenant filter will be added, leading to zero results in the queries.

    @Override
    public Tenant findWeak(final Long id) {
        if (id == null) {
            return null;
        }
        return this.entityManager.find(Tenant.class, id);
    }

    public Tenant findByName(final String name) {
        return findBy(TENANT_COLUMN_NAME, name, 1).stream().findFirst().orElse(null);
    }

    public List<Tenant> findByActive(final Boolean active) {
        final CriteriaQuery<Tenant> criteriaQuery = getCriteriaBuilder().createQuery(Tenant.class);
        final Root<Tenant> root = criteriaQuery.from(Tenant.class);
        Predicate predicate = getCriteriaBuilder().conjunction();

        if (active != null) {
            predicate = getCriteriaBuilder().and(predicate, root.get(DB_COLUMN_ACTIVE).in(List.of(active)));
        }

        criteriaQuery.where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}

