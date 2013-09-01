package jpaoletti.jpm2.core.service;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.PaginatedList;
import jpaoletti.jpm2.core.model.SessionEntityData;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

/**
 *
 * @author jpaoletti
 */
@Service
public class JPMServiceImpl extends JPMServiceBase implements JPMService {

    /**
     *
     * @param entity Weak entity
     * @param owner Instance of the owner entity.
     * @param ownerField
     * @return
     */
    @Override
    public PaginatedList getWeakList(Entity entity, String instanceId, Entity weak) throws PMException {
        final Object owner = entity.getDao().get(instanceId);
        final List list = weak.getDao().list(Restrictions.eq(weak.getOwner().getLocalProperty(), owner));
        final PaginatedList pl = new PaginatedList();
        pl.setTotal((long) list.size());
        pl.getContents().load(list, weak, weak.getOperation("list"));
        return pl;
    }

    @Override
    public PaginatedList getPaginatedList(Entity entity, Operation operation, SessionEntityData sessionEntityData, Integer page, Integer pageSize) throws PMException {
        entity.checkAuthorization();
        operation.checkAuthorization();
        final PaginatedList pl = new PaginatedList();
        final Criterion search = sessionEntityData.getSearchCriteria().getCriterion();
        if (entity.isPaginable()) {
            pl.setPageSize(pageSize != null ? pageSize : sessionEntityData.getPageSize());
            pl.setPage(page != null ? page : sessionEntityData.getPage());
            if (search == null) {
                pl.getContents().load(entity.getDao().list(pl.from(), pl.getPageSize()), entity, operation);
                if (entity.isCountable()) {
                    pl.setTotal(entity.getDao().count());
                }
            } else {
                pl.getContents().load(entity.getDao().list(pl.from(), pl.getPageSize(), search), entity, operation);
                if (entity.isCountable()) {
                    pl.setTotal(entity.getDao().count(search));
                }
            }
            sessionEntityData.setPage(pl.getPage());
            sessionEntityData.setPageSize(pl.getPageSize());
        } else {
            if (search == null) {
                pl.getContents().load(entity.getDao().list(), entity, operation);
            } else {
                pl.getContents().load(entity.getDao().list(search), entity, operation);
            }
        }
        for (Field field : entity.getAllFields()) {
            if (field.getSearcher() != null) {
                pl.getFieldSearchs().put(field, field.getSearcher().visualization());
            }
        }
        getJpm().audit();
        return pl;
    }

    @Override
    public Object update(Entity entity, Operation operation, String instanceId, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException {
        final Object object = entity.getDao().get(instanceId); //current object
        getContext().setObject(object);
        processFields(entity, operation, object, entityInstance, parameters);
        preExecute(operation, object);
        entity.getDao().update(object);
        postExecute(operation);
        getJpm().audit();
        return object;
    }

    @Override
    public void delete(Entity entity, Operation operation, String instanceId) throws PMException {
        final Object object = entity.getDao().get(instanceId); //current object
        getContext().setObject(object);
        preExecute(operation, object);
        entity.getDao().delete(object);
        postExecute(operation);
        getJpm().audit();
    }

    @Override
    public Object get(Entity entity, Operation operation, String instanceId) throws PMException {
        preExecute(operation, null);
        final Object object = entity.getDao().get(instanceId); //current object
        postExecute(operation);
        return object;
    }

    @Override
    public Object get(Entity entity, String instanceId) throws PMException {
        return entity.getDao().get(instanceId);
    }

    @Override
    public String save(Entity entity, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException {
        final Object object = JPMUtils.newInstance(entity.getClazz());
        processFields(entity, operation, object, entityInstance, parameters);
        preExecute(operation, object);
        entity.getDao().save(object);
        postExecute(operation);
        getContext().setObject(object);
        getJpm().audit();
        return entity.getDao().getId(object).toString();
    }

    @Override
    public String save(Entity owner, String ownerId, Entity entity, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException {
        final Object ownerObject = owner.getDao().get(ownerId);
        final Object object = JPMUtils.newInstance(entity.getClazz());
        JPMUtils.set(object, entity.getOwner().getLocalProperty(), ownerObject);
        processFields(entity, operation, object, entityInstance, parameters);
        preExecute(operation, object);
        entity.getDao().save(object);
        postExecute(operation);
        getContext().setObject(object);
        getJpm().audit();
        return entity.getDao().getId(object).toString();
    }
}
