package com.xxdai.starter.core.config;

import com.xxdai.pub.common.util.SpringUtil;
import com.xxdai.starter.cache.service.StatusService;
import com.xxdai.starter.cache.service.impl.IdempotenceServiceImpl;
import com.xxdai.starter.core.config.property.ClientCfgProperties;
import com.xxdai.starter.core.config.property.ProjectProperties;
import com.xxdai.starter.core.config.property.RabbitMqProperties;
import com.xxdai.starter.core.mq.rabbit.RabbitMsgSender;
import com.xxdai.starter.core.service.RabbitSampleService;
import com.xxdai.starter.core.service.impl.RabbitSampleServiceImpl;
import com.xxdai.starter.core.util.CheckerUtil;
import com.xxdai.starter.core.web.interceptor.CommonInterceptor;
import com.xxdai.starter.core.web.mapping.CustomRequestMappingHandlerMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

/**
 * 如果设置了 xxd.core.auto = false, 那么就不会进行自动配置
 * 需要检测到 RabbitMqConfig 存在才会生效。多数时候，@ConditionalOnClass 所指的
 * 类需要和本类有强关联性，比如本类依赖于所指的类所在的包。这里是为了示范而随意指定了一个。
 *
 * @author fangdajiang
 * @date 2017/12/18
 */
@Configuration
//@ConditionalOnClass({ RabbitMqConfig.class })
@ConditionalOnProperty(prefix = "xxd.core", name = "auto", havingValue = "true", matchIfMissing = true)
@EnableTransactionManagement
@EnableConfigurationProperties({ProjectProperties.class, ClientCfgProperties.class,})
@ServletComponentScan(value = "com.xxdai.starter.core.web.filter")
@Slf4j
@Order(1)
@Import(value={SpringUtil.class, Swagger2Config.class, SecurityPermitAllConfig.class})
public class StarterCoreAutoConfiguration extends XxdAutoConfiguration implements WebMvcConfigurer {

    /**
     * 是否启用 CommonInterceptor
     * 待改进
     */
    @Value("${project.common-interceptor.enabled}")
    private boolean commonInterceptorEnabled;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.debug("adding default resource handlers...");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
    }

    /**
     * 当 project.common-interceptor.enabled 为 true 时，添加 CommonInterceptor ；
     */
    public @Override void addInterceptors(InterceptorRegistry registry) {
        log.debug("commonInterceptorEnabled:{}", commonInterceptorEnabled);
        if (commonInterceptorEnabled) {
            log.info("Mapping CommonInterceptor to [/**], exclude swagger etc. and path patterns from yml settings");
            handlePathPatterns(registry.addInterceptor(new CommonInterceptor()));
        }
    }

    @Bean
    @Profile({"local","dev","test","fix"})
    public CheckerUtil nonProdCheckerUtil() {
        return new CheckerUtil(false);
    }

    @Bean
    @Profile({"stage","uat","prod"})
    public CheckerUtil prodCheckerUtil() {
        return new CheckerUtil(true);
    }

    /**
     * 使用自定义 RequestMappingHandlerMapping，以支持自定义注解实现 API 版本控制
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.main", name = "allow-bean-definition-overriding", havingValue = "true")
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping handlerMapping = new CustomRequestMappingHandlerMapping();
        handlerMapping.setOrder(0);
        return handlerMapping;
    }

    @Bean("statusService")
    public StatusService statusService(){
        return new IdempotenceServiceImpl();
    }

    @Bean
    @ConditionalOnBean({RabbitMqProperties.class})
    public RabbitSampleService rabbitSampleService(RabbitMsgSender rabbitMsgSender) {
        log.debug("initializing RabbitSampleService");
        return new RabbitSampleServiceImpl(rabbitMsgSender);
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

    }

    @Override
    public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {

    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry viewResolverRegistry) {

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> list) {

    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> list) {

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> list) {

    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> list) {

    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {

    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {

    }

    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer pathMatchConfigurer) {

    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer contentNegotiationConfigurer) {

    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer asyncSupportConfigurer) {

    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer defaultServletHandlerConfigurer) {
//        defaultServletHandlerConfigurer.enable();
    }

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {

    }

}
