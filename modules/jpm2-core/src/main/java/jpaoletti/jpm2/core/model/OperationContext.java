package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.PMException;

/**
 * This interface allows the programmer to defines some code to execute before
 * or after any operation execution.
 *
 * @author jpaoletti
 *
 */
public interface OperationContext {

    /**
     * This method is executed at the very beginning of the process, before
     * converterting or replace any data on objects.
     *
     * @throws PMException
     */
    public void preConversion() throws PMException;

    /**
     * This method is executed before trying to execute the main method of the
     * operation, that is before opening any transaction.
     *
     * @throws PMException
     */
    public void preExecute() throws PMException;

    /**
     * This method is executed after the main method of the operation.
     *
     * @throws PMException
     */
    public void postExecute() throws PMException;
}
