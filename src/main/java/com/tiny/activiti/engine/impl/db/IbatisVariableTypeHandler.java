package com.tiny.activiti.engine.impl.db;

import com.tiny.activiti.engine.ActivitiException;
import com.tiny.activiti.engine.impl.context.Context;
import com.tiny.activiti.engine.impl.variable.VariableType;
import com.tiny.activiti.engine.impl.variable.VariableTypes;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IbatisVariableTypeHandler implements TypeHandler<VariableType> {

    protected VariableTypes variableTypes;

    public VariableType getResult(ResultSet rs, String columnName) throws SQLException {
        String typeName = rs.getString(columnName);
        VariableType type = getVariableTypes().getVariableType(typeName);
        if (type == null && typeName != null) {
            throw new ActivitiException("unknown variable type name " + typeName);
        }
        return type;
    }

    public VariableType getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String typeName = cs.getString(columnIndex);
        VariableType type = getVariableTypes().getVariableType(typeName);
        if (type == null) {
            throw new ActivitiException("unknown variable type name " + typeName);
        }
        return type;
    }

    public void setParameter(PreparedStatement ps, int i, VariableType parameter, JdbcType jdbcType) throws SQLException {
        String typeName = parameter.getTypeName();
        ps.setString(i, typeName);
    }

    protected VariableTypes getVariableTypes() {
        if (variableTypes == null) {
            variableTypes = Context.getProcessEngineConfiguration().getVariableTypes();
        }
        return variableTypes;
    }

    public VariableType getResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String typeName = resultSet.getString(columnIndex);
        VariableType type = getVariableTypes().getVariableType(typeName);
        if (type == null) {
            throw new ActivitiException("unknown variable type name " + typeName);
        }
        return type;
    }
}
