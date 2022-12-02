package com.tiny.activiti.engine.impl.variable;

public interface VariableTypes {
    VariableType getVariableType(String typeName);

    /**
     * @return the variable type to be used to store the given value as a variable.
     *           When no available type is capable of storing the value.
     */
    VariableType findVariableType(Object value);

    VariableTypes addType(VariableType type);

    /**
     * Add type at the given index. The index is used when finding a type for an object. When different types can store a specific object value, the one with the smallest index will be used.
     */
    VariableTypes addType(VariableType type, int index);

    int getTypeIndex(VariableType type);

    int getTypeIndex(String typeName);

    VariableTypes removeType(VariableType type);
}
