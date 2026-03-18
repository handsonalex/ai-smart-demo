package com.smart.domain.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PgVector类型处理器
 *
 * @author Joseph Ho
 */
@MappedTypes(float[].class)
@MappedJdbcTypes(JdbcType.OTHER)
public class PgVectorTypeHandler extends BaseTypeHandler<float[]> {

    private static final String BRACKET_LEFT = "[";
    private static final String BRACKET_RIGHT = "]";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, float[] parameter, JdbcType jdbcType)
            throws SQLException {
        StringBuilder sb = new StringBuilder(BRACKET_LEFT);
        for (int j = 0; j < parameter.length; j++) {
            if (j > 0) {
                sb.append(",");
            }
            sb.append(parameter[j]);
        }
        sb.append(BRACKET_RIGHT);
        ps.setString(i, sb.toString());
    }

    @Override
    public float[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return parseVector(value);
    }

    @Override
    public float[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return parseVector(value);
    }

    @Override
    public float[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return parseVector(value);
    }

    private float[] parseVector(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // Remove surrounding brackets [ ]
        String trimmed = value.trim();
        if (trimmed.startsWith(BRACKET_LEFT)) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith(BRACKET_RIGHT)) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        String[] parts = trimmed.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }
}
