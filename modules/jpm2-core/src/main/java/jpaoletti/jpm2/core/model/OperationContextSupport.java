package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.PMException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Support class for OperationContext.
 *
 * @see OperationContext
 *
 * @author jpaoletti
 */
public class OperationContextSupport extends PMCoreObject implements OperationContext {

    @Autowired
    private JPMContext context;

    @Override
    public void preConversion() throws PMException {
    }

    @Override
    public void preExecute() throws PMException {
    }

    @Override
    public void postExecute() throws PMException {
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }
}
