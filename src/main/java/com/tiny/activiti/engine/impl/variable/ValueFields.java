package com.tiny.activiti.engine.impl.variable;

public interface ValueFields {

    /**
     * @return the name of the variable
     */
    String getName();

    /**
     * @return the process instance id of the variable
     */
    String getProcessInstanceId();

    /**
     * @return the execution id of the variable
     */
    String getExecutionId();

    /**
     * @return the task id of the variable
     */
    String getTaskId();

    /**
     * @return the first text value, if any, or null.
     */
    String getTextValue();

    /**
     * Sets the first text value. A value of null is allowed.
     */
    void setTextValue(String textValue);

    /**
     * @return the second text value, if any, or null.
     */
    String getTextValue2();

    /**
     * Sets second text value. A value of null is allowed.
     */
    void setTextValue2(String textValue2);

    /**
     * @return the long value, if any, or null.
     */
    Long getLongValue();

    /**
     * Sets the long value. A value of null is allowed.
     */
    void setLongValue(Long longValue);

    /**
     * @return the double value, if any, or null.
     */
    Double getDoubleValue();

    /**
     * Sets the double value. A value of null is allowed.
     */
    void setDoubleValue(Double doubleValue);

    /**
     * @return the byte array value, if any, or null.
     */
    byte[] getBytes();

    /**
     * Sets the byte array value. A value of null is allowed.
     */
    void setBytes(byte[] bytes);

    Object getCachedValue();

    void setCachedValue(Object cachedValue);
}
