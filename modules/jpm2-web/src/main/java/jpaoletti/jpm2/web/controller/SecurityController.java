package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.EntityPath;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.service.SecurityService;
import jpaoletti.jpm2.core.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Reset a user password and generates a new one.
 *
 * @author jpaoletti
 */
@Controller
public class SecurityController extends BaseController {

    public static final String OP_RESET_PASSWORD = "resetPassword";
    public static final String OP_PROFILE = "profile";
    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/jpm/{entityPath}/{instanceId}/" + OP_RESET_PASSWORD)
    public ModelAndView resetPassword(@PathVariable EntityPath entityPath, @PathVariable String instanceId) throws PMException {
        getContext().set(entityPath, OP_RESET_PASSWORD);
        final User user = getSecurityService().resetPassword(entityPath.getEntity(), getContext().getOperation(), instanceId);
        getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(instanceId, user), entityPath.getEntity(), getContext().getOperation()));
        return new ModelAndView("jpm-" + OP_RESET_PASSWORD);
    }

    @RequestMapping(value = "/jpm/{entityPath}/{instanceId}/" + OP_PROFILE)
    public ModelAndView profile(
            @PathVariable EntityPath entityPath,
            @PathVariable String instanceId,
            @RequestParam(required = false) String current,
            @RequestParam(required = false) String newpass) throws PMException {
        getContext().set(entityPath, OP_PROFILE);
        final IdentifiedObject iobject = getService().get(entityPath.getEntity(), getContext().getOperation(), instanceId);
        final UserDetails user = (UserDetails) iobject.getObject();
        if (!getUserDetails().getUsername().equals(user.getUsername())) {
            throw new NotAuthorizedException();
        }
        getContext().setEntityInstance(new EntityInstance(iobject, entityPath.getEntity(), getContext().getOperation()));
        if (current != null) {
            getSecurityService().changePassword(entityPath.getEntity(), getContext().getOperation(), instanceId, current, newpass);
        }
        return new ModelAndView("jpm-" + OP_PROFILE);
    }

    public SecurityService getSecurityService() {
        return securityService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
