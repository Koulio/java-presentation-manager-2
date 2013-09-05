package jpaoletti.jpm2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.NotAuthorizedException;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.SessionEntityData;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
public class BaseController {

    public static final String CURRENT_HOME = "currentHome";
    @Autowired
    public HttpSession session;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JPMContext context;
    @Autowired
    private PresentationManager jpm;
    @Autowired
    private MessageSource messageSource;
    //Messages TO-DO
    private List<Message> globalMessages = new ArrayList<>();

    public UserDetails getUserDetails() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        } else {
            return null;
        }
    }

    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public SessionEntityData getSessionEntityData(Entity entity) {
        final Object sed = getSession().getAttribute(entity.getId());
        if (sed == null) {
            final SessionEntityData sessionEntityData = new SessionEntityData(entity);
            getSession().setAttribute(entity.getId(), sessionEntityData);
        }
        return (SessionEntityData) getSession().getAttribute(entity.getId());
    }

    protected EntityInstance newEntityInstance(String instanceId, Entity entity) throws PMException {
        return new EntityInstance(instanceId, entity, getContext().getOperation(), getContext().getObject());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleTypeMismatchException(MissingServletRequestParameterException ex, HttpServletRequest req, HttpServletResponse resp) {
        JPMUtils.getLogger().warn("Parameter failure: " + ex.getRootCause().getLocalizedMessage());
        JPMUtils.getLogger().warn("Invalid name is: " + ex.getParameterName());
        JPMUtils.getLogger().warn("Required type is: " + ex.getParameterType());
        return null;
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ModelAndView handleTypeMismatchException(TypeMismatchException ex, HttpServletRequest req, HttpServletResponse resp) {
        JPMUtils.getLogger().warn("Parameter failure: " + ex.getRootCause().getLocalizedMessage());
        JPMUtils.getLogger().warn("Invalid value is: " + ex.getValue());
        JPMUtils.getLogger().warn("Required type is: " + ex.getRequiredType().getSimpleName());
        return null;
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public String handleNotAuthorizedException() {
        return "not-authotized";
    }

    public void setCurrentHome(String newHome) {
        getSession().setAttribute(CURRENT_HOME, newHome);
    }

    protected void prepareItemOperation(Entity entity, String instanceId, String operationId) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation(operationId));
        if (instanceId != null) {
            getContext().setObject(entity.getDao().get(instanceId));
            getContext().setEntityInstance(newEntityInstance(instanceId, entity));
        }
    }

    /**
     * Redirect to list depending on weaks entities.
     */
    protected String toList(final EntityInstance instance, Entity entity) {
        if (instance.getOwner() != null) {
            return String.format("redirect:/jpm/%s/%s/%s", instance.getOwner().getId(), instance.getOwnerId(), entity.getId());
        } else {
            return String.format("redirect:/jpm/%s", entity.getId());
        }
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }

    public List<Message> getGlobalMessages() {
        return globalMessages;
    }

    public void setGlobalMessages(List<Message> globalMessages) {
        this.globalMessages = globalMessages;
    }

    public PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
