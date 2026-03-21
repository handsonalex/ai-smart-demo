package com.smart.infrastructure.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * PostgreSQL 数据源配置（副数据源）
 *
 * <p>本项目中 PostgreSQL 专门用于 RAG（检索增强生成）模块的向量存储，
 * 利用 pgvector 扩展实现高效的向量相似度检索。</p>
 *
 * <p>与 MySQL 主数据源的区别：</p>
 * <ul>
 *   <li>没有 @Primary 注解，属于副数据源，必须通过 @Qualifier 显式指定</li>
 *   <li>仅扫描 com.smart.domain.mapper.chunk 子包下的 Mapper（即知识分片相关 Mapper）</li>
 *   <li>配置属性前缀为 spring.datasource.postgres，与 MySQL 数据源隔离</li>
 * </ul>
 *
 * <p>多数据源隔离策略：通过不同的 @MapperScan basePackages 将 Mapper 接口分配到不同数据源，
 * 避免跨库误操作。MySQL 管理业务 Mapper，PostgreSQL 管理向量存储 Mapper。</p>
 *
 * @author Joseph Ho
 */
@Configuration
@MapperScan(
        basePackages = "com.smart.domain.mapper.chunk",
        sqlSessionFactoryRef = "postgresSqlSessionFactory",
        sqlSessionTemplateRef = "postgresSqlSessionTemplate"
)
public class PostgresDataSourceConfig {

    /**
     * 创建 PostgreSQL 数据源 Bean
     *
     * <p>注意：此处没有 @Primary 注解，因此在需要注入此数据源时，
     * 必须使用 @Qualifier("postgresDataSource") 进行显式指定。</p>
     *
     * <p>@ConfigurationProperties 将 application.yml 中 spring.datasource.postgres 前缀下的属性
     * 自动绑定到 HikariDataSource（如 jdbc-url、username、password 等）。</p>
     *
     * @return PostgreSQL HikariCP 数据源实例
     */
    @Bean(name = "postgresDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.postgres")
    public DataSource postgresDataSource() {
        return new HikariDataSource();
    }

    /**
     * 创建 PostgreSQL 的 SqlSessionFactory
     *
     * <p>通过 @Qualifier 显式注入 PostgreSQL 数据源，避免因 @Primary 机制误注入 MySQL 数据源。</p>
     *
     * @param postgresDataSource PostgreSQL 数据源（通过 @Qualifier 显式指定）
     * @return MyBatis-Plus 的 SqlSessionFactory 实例
     * @throws Exception 如果创建 SqlSessionFactory 失败
     */
    @Bean(name = "postgresSqlSessionFactory")
    public SqlSessionFactory postgresSqlSessionFactory(
            @Qualifier("postgresDataSource") DataSource postgresDataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(postgresDataSource);
        factoryBean.setTypeHandlersPackage("com.smart.domain.handler");
        return factoryBean.getObject();
    }

    /**
     * 创建 PostgreSQL 的 SqlSessionTemplate
     *
     * @param postgresSqlSessionFactory PostgreSQL 的 SqlSessionFactory（通过 @Qualifier 显式指定）
     * @return SqlSessionTemplate 实例
     */
    @Bean(name = "postgresSqlSessionTemplate")
    public SqlSessionTemplate postgresSqlSessionTemplate(
            @Qualifier("postgresSqlSessionFactory") SqlSessionFactory postgresSqlSessionFactory) {
        return new SqlSessionTemplate(postgresSqlSessionFactory);
    }

    /**
     * 创建 PostgreSQL 的事务管理器
     *
     * <p>使用此事务管理器时需要显式指定，例如：
     * {@code @Transactional(transactionManager = "postgresTransactionManager")}</p>
     *
     * @param postgresDataSource PostgreSQL 数据源（通过 @Qualifier 显式指定）
     * @return 基于 JDBC DataSource 的事务管理器
     */
    @Bean(name = "postgresTransactionManager")
    public PlatformTransactionManager postgresTransactionManager(
            @Qualifier("postgresDataSource") DataSource postgresDataSource) {
        return new DataSourceTransactionManager(postgresDataSource);
    }
}
