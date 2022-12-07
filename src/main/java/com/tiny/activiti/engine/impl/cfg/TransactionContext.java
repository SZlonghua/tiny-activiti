package com.tiny.activiti.engine.impl.cfg;

public interface TransactionContext {

    void commit();

    void rollback();

    void addTransactionListener(TransactionState transactionState, TransactionListener transactionListener);

}
