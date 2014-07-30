package jpaoletti.jpm2.core.dao;

import java.io.Serializable;
import java.util.List;
import jpaoletti.jpm2.core.idtransformer.IdTransformer;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.jodah.typetools.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author jpaoletti
 * @param <T>
 * @param <ID>
 */
public abstract class GenericDAO<T, ID extends Serializable> implements DAO<T, ID> {

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;
    private Class<T> persistentClass;
    protected Class<ID> idClass;
    private IdTransformer<ID> transformer;

    public GenericDAO() {
        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(GenericDAO.class, getClass());
        this.persistentClass = (Class<T>) typeArguments[0];
        this.idClass = (Class<ID>) typeArguments[1];
    }

    @Override
    public T get(String id) {
        if (id.getClass().equals(idClass)) {
            return (T) getSession().get(getPersistentClass(), id);
        } else {
            try {
                return (T) getSession().get(getPersistentClass(), (Serializable) getTransformer().transform(id));
            } catch (Exception e) {
                JPMUtils.getLogger().warn("Invalid ID transformation", e);
                return null;
            }
        }
    }

    @Override
    public void update(final Object object) {
        getSession().update(object);
    }

    @Override
    public void save(Object object) {
        getSession().save(object);
    }

    @Override
    public void delete(Object object) {
        getSession().delete(object);
    }

    /**
     * Deletes all.
     */
    public void deleteAll() {
        getSession().createQuery("DELETE FROM " + getPersistentClass().getName()).executeUpdate();
    }

    @Override
    public Long count(DAOListConfiguration configuration) {
        final Criteria c = getBaseCriteria(configuration);
        c.setProjection(Projections.rowCount());
        return (Long) c.uniqueResult();
    }

    @Override
    public List<T> list(DAOListConfiguration configuration) {
        final Criteria c = getBaseCriteria(configuration);
        if (configuration.getMax() != null) {
            c.setMaxResults(configuration.getMax());
        }
        if (configuration.getFrom() != null) {
            c.setFirstResult(configuration.getFrom());
        }
        if (!configuration.getOrders().isEmpty()) {
            for (Order order : configuration.getOrders()) {
                c.addOrder(order);
            }
        }
        return c.list();
    }

    protected Criteria getBaseCriteria(DAOListConfiguration configuration) {
        Criteria c = getSession().createCriteria(getPersistentClass());
        c.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        for (DAOListConfiguration.DAOListConfigurationAlias alias : configuration.getAliases()) {
            if (alias.getJoinType() != null) {
                c = c.createAlias(alias.getProperty(), alias.getAlias(), alias.getJoinType());
            } else {
                c = c.createAlias(alias.getProperty(), alias.getAlias());
            }
        }
        for (Criterion criterion : configuration.getRestrictions()) {
            if (criterion != null) {
                c.add(criterion);
            }
        }
        return c;
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public IdTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(IdTransformer transformer) {
        this.transformer = transformer;
    }

    /**
     * @return the persistentClass
     */
    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    /**
     * @param persistentClass the persistentClass to set
     */
    public void setPersistentClass(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    @Override
    public void detach(Object object) {
        getSession().evict(object);
    }
}
