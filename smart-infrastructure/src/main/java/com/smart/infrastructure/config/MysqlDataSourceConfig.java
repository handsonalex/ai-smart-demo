package com.smart.infrastructure.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * MySQL 数据源配置（主数据源）
 *
 * <p>本项目采用多数据源架构：MySQL 负责业务数据（场景、规则、决策记录等），
 * PostgreSQL 负责向量存储（pgvector 知识分片）。
 * 本类配置 MySQL 作为 @Primary 主数据源，Spring 在无法区分多个同类型 Bean 时优先使用此数据源。</p>
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li>使用 HikariCP 连接池（Spring Boot 默认推荐的高性能连接池）</li>
 *   <li>通过 @MapperScan 将 com.smart.domain.mapper 包下的 Mapper 接口绑定到 MySQL 数据源</li>
 *   <li>使用 MybatisSqlSessionFactoryBean（MyBatis-Plus 增强版）替代原生 SqlSessionFactoryBean，
 *       以支持 MyBatis-Plus 的分页、逻辑删除等高级特性</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Configuration
@MapperScan(
        basePackages = {
                "com.smart.domain.mapper"
        },
        sqlSessionFactoryRef = "mysqlSqlSessionFactory",
        sqlSessionTemplateRef = "mysqlSqlSessionTemplate"
)
public class MysqlDataSourceConfig {

    /**
     * 创建 MySQL 数据源 Bean
     *
     * <p>@Primary 标记此数据源为主数据源，当其他组件（如 Spring 事务管理器）
     * 需要注入 DataSource 但未指定具体 Bean 名称时，会默认使用此数据源。</p>
     *
     * <p>@ConfigurationProperties 自动将 application.yml 中 spring.datasource.mysql 前缀下的属性
     * （如 jdbc-url、username、password、maximum-pool-size 等）绑定到 HikariDataSource。</p>
     *
     * @return MySQL HikariCP 数据源实例
     */
    @Primary
    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource mysqlDataSource() {
        return new HikariDataSource();
    }

    /**
     * 创建 MySQL 的 SqlSessionFactory
     *
     * <p>@Primary 确保在多数据源场景下，MyBatis-Plus 默认使用此 SqlSessionFactory。</p>
     *
     * <p>setTypeHandlersPackage 注册自定义类型处理器包路径，用于处理特殊的 Java 类型与数据库类型之间的映射
     * （如枚举类型转换、JSON 字段映射等）。</p>
     *
     * @param mysqlDataSource MySQL 数据源（由 Spring 自动注入 @Primary 数据源）
     * @return MyBatis-Plus 的 SqlSessionFactory 实例
     * @throws Exception 如果创建 SqlSessionFactory 失败
     */
    @Primary
    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory mysqlSqlSessionFactory(DataSource mysqlDataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(mysqlDataSource);
        factoryBean.setTypeHandlersPackage("com.smart.domain.handler");
        return factoryBean.getObject();
    }

    /**
     * 创建 MySQL 的 SqlSessionTemplate
     *
     * <p>SqlSessionTemplate 是 MyBatis-Spring 的核心类，它是线程安全的，
     * 可以在多个 DAO 之间共享。它管理 SqlSession 的生命周期，包括关闭、提交或回滚。</p>
     *
     * @param mysqlSqlSessionFactory MySQL 的 SqlSessionFactory
     * @return SqlSessionTemplate 实例
     */
    @Primary
    @Bean(name = "mysqlSqlSessionTemplate")
    public SqlSessionTemplate mysqlSqlSessionTemplate(SqlSessionFactory mysqlSqlSessionFactory) {
        return new SqlSessionTemplate(mysqlSqlSessionFactory);
    }

    /**
     * 创建 MySQL 的事务管理器
     *
     * <p>@Primary 确保在使用 @Transactional 注解时，如果未指定 transactionManager，
     * 默认使用 MySQL 的事务管理器。</p>
     *
     * @param mysqlDataSource MySQL 数据源
     * @return 基于 JDBC DataSource 的事务管理器
     */
    @Primary
    @Bean(name = "mysqlTransactionManager")
    public PlatformTransactionManager mysqlTransactionManager(DataSource mysqlDataSource) {
        return new DataSourceTransactionManager(mysqlDataSource);
    }
}
