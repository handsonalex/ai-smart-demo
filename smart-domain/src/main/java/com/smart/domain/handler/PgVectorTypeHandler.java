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
 * PostgreSQL pgvector类型与Java float数组之间的MyBatis类型处理器。
 * <p>
 * 本处理器解决了MyBatis无法直接处理pgvector自定义类型的问题，负责在以下两种格式之间进行转换：
 * <ul>
 *   <li><b>Java端</b>：float[] 数组，如 {0.1f, 0.2f, 0.3f}</li>
 *   <li><b>数据库端</b>：pgvector字符串格式，如 "[0.1,0.2,0.3]"</li>
 * </ul>
 * </p>
 * <p>
 * <b>pgvector存储格式说明</b>：
 * pgvector是PostgreSQL的向量扩展，用于存储和检索高维浮点向量。
 * 在数据库中，向量以字符串形式表示，格式为方括号包裹、逗号分隔的浮点数列表，如 "[0.1,0.2,0.3]"。
 * pgvector支持多种距离计算操作符：
 * <ul>
 *   <li>{@code <->} 欧氏距离（L2距离）</li>
 *   <li>{@code <=>} 余弦距离</li>
 *   <li>{@code <#>} 内积距离（负内积）</li>
 * </ul>
 * </p>
 * <p>
 * <b>使用方式</b>：
 * 在实体类的向量字段上通过 {@code @TableField(typeHandler = PgVectorTypeHandler.class)} 注解引用，
 * 同时实体类需要设置 {@code @TableName(autoResultMap = true)} 以确保查询结果能正确映射。
 * </p>
 *
 * @author Joseph Ho
 * @see com.smart.domain.entity.KnowledgeChunk#embedding
 */
@MappedTypes(float[].class)
@MappedJdbcTypes(JdbcType.OTHER)
public class PgVectorTypeHandler extends BaseTypeHandler<float[]> {

    /** pgvector字符串格式的左方括号 */
    private static final String BRACKET_LEFT = "[";
    /** pgvector字符串格式的右方括号 */
    private static final String BRACKET_RIGHT = "]";

    /**
     * 将Java float数组转换为pgvector字符串格式，并设置到PreparedStatement中。
     * <p>
     * 转换过程：float[] {0.1, 0.2, 0.3} -> 字符串 "[0.1,0.2,0.3]"
     * 通过 ps.setString() 设置参数，PostgreSQL的pgvector扩展会自动将字符串解析为vector类型。
     * </p>
     *
     * @param ps        预编译SQL语句对象
     * @param i         参数位置索引（从1开始）
     * @param parameter Java端的float数组
     * @param jdbcType  JDBC类型（此处为OTHER）
     * @throws SQLException SQL操作异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, float[] parameter, JdbcType jdbcType)
            throws SQLException {
        // 构建pgvector字符串格式：[val1,val2,val3,...]
        StringBuilder sb = new StringBuilder(BRACKET_LEFT);
        for (int j = 0; j < parameter.length; j++) {
            if (j > 0) {
                sb.append(",");
            }
            sb.append(parameter[j]);
        }
        sb.append(BRACKET_RIGHT);
        // 以字符串形式设置参数，pgvector会在数据库端进行类型转换
        ps.setString(i, sb.toString());
    }

    /**
     * 从ResultSet中按列名读取pgvector字符串，并转换为Java float数组。
     *
     * @param rs         结果集
     * @param columnName 列名
     * @return 转换后的float数组，若数据库值为NULL则返回null
     * @throws SQLException SQL操作异常
     */
    @Override
    public float[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return parseVector(value);
    }

    /**
     * 从ResultSet中按列索引读取pgvector字符串，并转换为Java float数组。
     *
     * @param rs          结果集
     * @param columnIndex 列索引（从1开始）
     * @return 转换后的float数组，若数据库值为NULL则返回null
     * @throws SQLException SQL操作异常
     */
    @Override
    public float[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return parseVector(value);
    }

    /**
     * 从CallableStatement中按列索引读取pgvector字符串，并转换为Java float数组。
     * 用于存储过程调用场景。
     *
     * @param cs          可调用语句对象
     * @param columnIndex 列索引（从1开始）
     * @return 转换后的float数组，若数据库值为NULL则返回null
     * @throws SQLException SQL操作异常
     */
    @Override
    public float[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return parseVector(value);
    }

    /**
     * 将pgvector字符串格式解析为Java float数组。
     * <p>
     * 解析过程：字符串 "[0.1,0.2,0.3]" -> float[] {0.1, 0.2, 0.3}
     * <ol>
     *   <li>去除首尾的方括号 "[" 和 "]"</li>
     *   <li>按逗号分隔得到各维度的字符串表示</li>
     *   <li>逐个解析为float值并组装为数组</li>
     * </ol>
     * </p>
     *
     * @param value pgvector格式的字符串，如 "[0.1,0.2,0.3]"
     * @return 解析后的float数组，输入为null或空字符串时返回null
     */
    private float[] parseVector(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // 去除pgvector字符串首尾的方括号
        String trimmed = value.trim();
        if (trimmed.startsWith(BRACKET_LEFT)) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith(BRACKET_RIGHT)) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        // 按逗号分隔，逐个解析为float值
        String[] parts = trimmed.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }
}
