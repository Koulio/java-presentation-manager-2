package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.dao.DAO;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;

/**
 * Helper to contain the context and the entity.
 *
 * @author jpaoletti
 */
public class ContextualEntity extends PMCoreObject {

    private Entity entity;
    private String context;

    public ContextualEntity(Entity entity, String context) {
        this.entity = entity;
        this.context = context;
    }

    @Override
    public void checkAuthorization() throws NotAuthorizedException {
        getEntity().checkAuthorization();
        final EntityContext ctx = getEntity().getContext(getContext());
        if (ctx != null) {
            ctx.checkAuthorization();
        }
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder(getEntity().getId());
        if (getContext() != null) {
            res.append(PresentationManager.CONTEXT_SEPARATOR).append(getContext());
        }
        return res.toString();
    }

    public DAO getDao() {
        return getEntity().getDao(getContext());
    }

    public EntityOwner getOwner() {
        return getEntity().getOwner(getContext());
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
