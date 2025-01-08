package com.raynigon.ecs.logging.access.server;

import com.raynigon.ecs.logging.access.AccessLogProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;

@Configuration
@AutoConfiguration
@Order(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore(ServletWebServerFactoryAutoConfiguration.class)
@ConditionalOnClass(value = {WebServerFactoryCustomizer.class})
@EnableConfigurationProperties(AccessLogProperties.class)
public class AccessLogFilterConfiguration {


    @Bean
    @NonNull
    @ConditionalOnProperty(value = "raynigon.logging.access.export-body", havingValue = "true")
    public FilterRegistrationBean<CustomTeeFilter> requestLoggingFilter() {
        return new FilterRegistrationBean<>(new CustomTeeFilter());
    }
}
