package jpaoletti.jpm2.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jpaoletti.jpm2.web.controller.BaseController;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
public class JPMIterceptor implements HandlerInterceptor {

    @Autowired
    private JPMContext context;

    @Override
    public boolean preHandle(HttpServletRequest hsr, HttpServletResponse hsr1, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest hsr, HttpServletResponse hsr1, Object o, ModelAndView mav) throws Exception {
        final JPMContext ctx = getContext();
        if (ctx != null) {
            final Entity entity = ctx.getEntity();
            final Operation operation = ctx.getOperation();
            if (mav != null) {
                mav.addObject("locale", LocaleContextHolder.getLocale());
                if (entity != null) {
                    mav.addObject("entity", entity);
                    final EntityInstance instance = ctx.getEntityInstance();
                    mav.addObject("instance", instance);
                    if (operation != null) {
                        mav.addObject("operation", operation);
                        mav.addObject("generalOperations", entity.getOperationsFor(instance, operation, OperationScope.GENERAL));
                        mav.addObject("selectedOperations", entity.getOperationsFor(instance, operation, OperationScope.SELECTED));
                    }
                    if (instance != null) {
                        if (instance.getIobject() != null) {
                            final Object object = instance.getIobject().getObject();
                            if (object != null) {
                                mav.addObject("object", object);
                                if (operation != null) {
                                    mav.addObject("itemOperations", entity.getOperationsFor(instance, operation, OperationScope.ITEM));
                                }
                            }
                        }
                        if (entity.isWeak() && instance.getOwner() != null) {
                            mav.addObject("owner", instance.getOwner().getEntity());
                            mav.addObject("ownerId", instance.getOwnerId());
                        }
                    }
                    if (!ctx.getEntityMessages().isEmpty()) {
                        mav.addObject("entityMessages", ctx.getEntityMessages());
                    }
                    if (!ctx.getFieldMessages().isEmpty()) {
                        mav.addObject("fieldMessages", ctx.getFieldMessages());
                    }
                }
            }
            if (entity != null) {
                hsr.getSession().setAttribute(BaseController.CURRENT_HOME, ctx.getEntity().getHome());
            }
        }
        try {
            if (hsr.getSession().getAttribute(BaseController.CURRENT_HOME) == null) {
                hsr.getSession().setAttribute(BaseController.CURRENT_HOME, "index");
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest hsr, HttpServletResponse hsr1, Object o, Exception excptn) throws Exception {
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }
}
