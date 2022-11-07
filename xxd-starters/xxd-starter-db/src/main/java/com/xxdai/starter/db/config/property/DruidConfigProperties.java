package com.xxdai.starter.db.config.property;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * 配置 Druid
 * Created by yq on 2017/3/26.
 */
@Configuration
@Validated @Data @ConfigurationProperties(prefix = "spring.datasource")
@ConditionalOnProperty(name = "spring.datasource.type",havingValue = "com.alibaba.druid.pool.DruidDataSource")
public class DruidConfigProperties {
    /**
     * 数据源类型
     */
    private String type;
    private String driverClassName;
    private String name;
    private String url;
    private String username;
    private String password;
    /**
     * 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
     */
    @NotNull private Integer initialSize;
    @NotNull private Integer minIdle;
    @NotNull private Integer maxActive;
    /**
     * 获取连接时最大等待时间，单位毫秒
     */
    @NotNull private Long maxWait;
    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private Long timeBetweenEvictionRunsMillis;
    /**
     * 连接保持空闲而不被驱逐的最长时间，即配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private Long minEvictableIdleTimeMillis;
    /**
     * 用来检测连接是否有效的sql，要求是一个查询语句
     * 如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会其作用
     */
    private String validationQuery;
    /**
     * 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效
     */
    private Boolean testWhileIdle;
    /**
     * 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
     */
    private Boolean testOnBorrow;
    /**
     * 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
     */
    private Boolean testOnReturn;
    private Boolean removeAbandoned;
    private Integer removeAbandonedTimeout;
    private Boolean logAbandoned;
    /**
     * 是否缓存preparedStatement，即PSCache
     */
    private Boolean poolPreparedStatements;
    /**
     * 指定每个连接上PSCache的大小。
     * 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true
     */
    private Integer maxPoolPreparedStatementPerConnectionSize;
    /**
     * 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
     */
    private String filters;
    /**
     * 通过connectProperties属性来打开mergeSql功能；慢SQL记录
     */
     private String connectionProperties;
    /**
     * 合并多个DruidDataSource的监控数据
     */
    private Boolean useGlobalDataSourceStat;
    /**
     * 监控配置项
     */
    private String monitorUserName;
    private String monitorPassword;
    private String monitorResetEnable;
    private String monitorAllow;
    private String monitorDeny;

    /**
     * 公钥
     */
    private String publickey;
}
