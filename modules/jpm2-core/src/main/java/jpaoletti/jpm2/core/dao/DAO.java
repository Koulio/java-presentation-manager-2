package jpaoletti.jpm2.core.dao;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author jpaoletti
 * @param <T>
 * @param <ID>
 */
public interface DAO<T, ID extends Serializable> {

    public Long count(DAOListConfiguration configuration);

    public void delete(Object object);

    public T get(String id);

    public ID getId(Object object);

    public List<T> list(DAOListConfiguration configuration);

    public void save(Object object);

    public void update(final Object object);

    public void detach(Object object);
}
