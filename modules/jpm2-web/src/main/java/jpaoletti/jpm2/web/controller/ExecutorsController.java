package jpaoletti.jpm2.web.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.AsynchronicOperationExecutor;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.OperationExecutor;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for generic executors.
 *
 * @author jpaoletti
 */
@Controller
public class ExecutorsController extends BaseController implements Observer {

    public static final String HTTP_SERVLET_REQUEST = "HTTP_SERVLET_REQUEST";
    public static final String OWNER_ENTITY = "OWNER_ENTITY";
    public static final String OWNER_ID = "OWNER_ID";

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * GET method prepares form.
     *
     * @param request
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = {"/jpm/{owner}/{ownerId}/{entity}/{operationId}.exec", "/jpm/{entity}/{operationId}.exec"}, method = RequestMethod.GET)
    public ModelAndView executorsGeneralPrepare(HttpServletRequest request) throws PMException {
        final Map<String, Object> preparation = getExecutor().prepare(new ArrayList<>());
        if (getExecutor().immediateExecute()) {
            executorsCommit(request, new ArrayList<>(), false);
            getContext().setGlobalMessage(MessageFactory.success("jpm." + getContext().getOperation().getId() + ".success"));
            return next(getContext().getEntity(), getContext().getOperation(), "", getExecutor().getDefaultNextOperationId());
        } else {
            final ModelAndView mav = new ModelAndView("op-" + getContext().getOperation().getId());
            preparation.entrySet().stream().forEach(
                    e -> mav.addObject(e.getKey(), e.getValue())
            );
            getRequest().getParameterMap().keySet().stream().forEach(
                    key -> mav.addObject((String) key, (String[]) getRequest().getParameterValues((String) key))
            );
            return mav;
        }
    }

    @RequestMapping(value = {"/jpm/{owner}/{ownerId}/{entity}/{operationId}.exec"}, method = RequestMethod.POST)
    @ResponseBody
    public JPMPostResponse executorsGeneralCommit(
            HttpServletRequest request,
            @PathVariable Entity owner, @PathVariable String ownerId,
            @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        final List<EntityInstance> instances = new ArrayList<>();
        final Map parameterMap = new LinkedHashMap(request.getParameterMap());
        parameterMap.put(HTTP_SERVLET_REQUEST, request);
        parameterMap.put(OWNER_ENTITY, owner);
        parameterMap.put(OWNER_ID, ownerId);
        final Map parameters = getExecutor().preExecute(getContext(), instances, parameterMap);
        return executorsGeneralCommit(instances, parameters, repeat);
    }

    @RequestMapping(value = {"/jpm/{entity}/{operationId}.exec"}, method = RequestMethod.POST)
    @ResponseBody
    public JPMPostResponse executorsGeneralCommit(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        final List<EntityInstance> instances = new ArrayList<>();
        final Map parameterMap = new LinkedHashMap(request.getParameterMap());
        parameterMap.put(HTTP_SERVLET_REQUEST, request);
        final Map parameters = getExecutor().preExecute(getContext(), instances, parameterMap);
        return executorsGeneralCommit(instances, parameters, repeat);
    }

    protected JPMPostResponse executorsGeneralCommit(final List<EntityInstance> instances, final Map parameters, boolean repeat) {
        try {
            if (getContext().getOperation().isSynchronic()) {
                getExecutor().execute(getContext(), instances, parameters, new Progress());
            } else if (!getJpm().registerAsynchronicExecutor(getContext(), getExecutor(), instances, parameters)) {
                throw new PMException("unable.to.register.asynchronic.executor");
            }
            String buildRedirect;
            if (repeat) {
                buildRedirect = buildRedirect((Entity) parameters.get(OWNER_ENTITY), (String) parameters.get(OWNER_ID), getContext().getEntity(), null, getContext().getOperation().getPathId(), "repeated=true");
            } else {
                buildRedirect = next(getContext().getEntity(), getContext().getOperation(), (Entity) parameters.get(OWNER_ENTITY), (String) parameters.get(OWNER_ID), getExecutor().getDefaultNextOperationId()).getViewName();
            }
            return new JPMPostResponse(true, buildRedirect, MessageFactory.success("jpm." + getContext().getOperation().getId() + ".success"));
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        } catch (PMException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        } catch (Exception e) {
            getContext().getEntityMessages().add(MessageFactory.error(e.getMessage()));
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        }
    }

    /**
     * GET method prepares form.
     *
     * @param request
     * @param instanceIds
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/{instanceIds}/{operationId}.exec", method = RequestMethod.GET)
    public ModelAndView executorsPrepare(HttpServletRequest request, @PathVariable List<String> instanceIds) throws PMException {
        final List<EntityInstance> instances = new ArrayList<>();
        for (String instanceId : instanceIds) {
            initItemControllerOperation(instanceId);
            instances.add(getContext().getEntityInstance());
        }
        final Map<String, Object> preparation = getExecutor().prepare(instances);
        if (getExecutor().immediateExecute()) {
            executorsCommit(request, instanceIds, false);
            getContext().setGlobalMessage(MessageFactory.success("jpm." + getContext().getOperation().getId() + ".success"));
            return next(getContext().getEntity(), getContext().getOperation(), StringUtils.join(instanceIds, ","), getExecutor().getDefaultNextOperationId());
        } else {
            final ModelAndView mav = new ModelAndView("op-" + getContext().getOperation().getId());
            preparation.entrySet().stream().forEach(
                    e -> mav.addObject(e.getKey(), e.getValue())
            );
            getRequest().getParameterMap().keySet().stream().forEach(
                    key -> mav.addObject((String) key, (String[]) getRequest().getParameterValues((String) key))
            );
            return mav;
        }
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceIds}/{operationId}.exec", method = RequestMethod.POST)
    @ResponseBody
    public JPMPostResponse executorsCommit(HttpServletRequest request, @PathVariable List<String> instanceIds, @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        try {
            final List<EntityInstance> instances = new ArrayList<>();
            for (String instanceId : instanceIds) {
                initItemControllerOperation(instanceId);
                instances.add(getContext().getEntityInstance());
            }
            final Map parameterMap = new LinkedHashMap(request.getParameterMap());
            parameterMap.put(HTTP_SERVLET_REQUEST, request);
            final Map parameters = getExecutor().preExecute(getContext(), instances, parameterMap);
            if (getContext().getOperation().isSynchronic()) {
                getExecutor().execute(getContext(), instances, parameters, new Progress());
            } else if (!getJpm().registerAsynchronicExecutor(getContext(), getExecutor(), instances, parameters)) {
                throw new PMException("unable.to.register.asynchronic.executor");
            }
            String buildRedirect;
            final String newIds = StringUtils.join(instances.stream().map(EntityInstance::getId).collect(Collectors.toList()), ",");
            if (repeat) {
                buildRedirect = buildRedirect(null, null, getContext().getEntity(), newIds, getContext().getOperation().getPathId(), "repeated=true");
            } else {
                //BUG follows list in weak not working
                buildRedirect = next(getContext().getEntity(), getContext().getOperation(), newIds, getExecutor().getDefaultNextOperationId()).getViewName();
            }
            return new JPMPostResponse(true, buildRedirect, MessageFactory.success("jpm." + getContext().getOperation().getId() + ".success"));
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            final IdentifiedObject iobject = new IdentifiedObject(StringUtils.join(instanceIds, ","), e.getValidatedObject());
            getContext().setEntityInstance(new EntityInstance(iobject, getContext()));
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        } catch (PMException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            } else {
                e.printStackTrace();
            }
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        } catch (Exception e) {
            getContext().getEntityMessages().add(MessageFactory.error(e.getMessage()));
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        }
    }

    @MessageMapping("/asynchronicOperationExecutorProgress")
    public void asynchronicOperationExecutorProgress(MsgId message) throws Exception {
        final String id = message.toString();
        if (getJpm().getAsynchronicOperationExecutor(id) != null) {
            getJpm().getAsynchronicOperationExecutor(id).addObserver(this);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            if (o instanceof AsynchronicOperationExecutor) {
                final Boolean ended = (Boolean) arg;
                final AsynchronicOperationExecutor t = (AsynchronicOperationExecutor) o;
                if (t.getId().contains(",")) {
                    for (String id : t.getId().split(",")) {
                        template.convertAndSend(
                                "/asynchronicOperationExecutor/" + (ended ? "done" : "progress") + "/" + id,
                                getJpm().getAsynchronicOperationExecutor(id).getProgress()
                        );
                    }
                } else {
                    template.convertAndSend(
                            "/asynchronicOperationExecutor/" + (ended ? "done" : "progress") + "/" + t.getId(),
                            getJpm().getAsynchronicOperationExecutor(t.getId()).getProgress()
                    );
                }
            }
        } catch (Exception e) {
            JPMUtils.getLogger().error("Error in ExecutorsController.update", e);
        }
    }

    protected OperationExecutor getExecutor() {
        return getContext().getOperation().getExecutor();
    }

    public static class MsgId {

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return getId();
        }
    }
}
