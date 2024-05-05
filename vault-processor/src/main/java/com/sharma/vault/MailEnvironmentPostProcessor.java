package com.sharma.vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.LinkedHashMap;
import java.util.Map;


public class MailEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 10;
    private int order = DEFAULT_ORDER;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication springApplication) {
        System.out.println("======================= Start =========================");
        System.out.println("Adding Properties");
        PropertySource<?> osEnvironmentPropertySource = environment.getPropertySources()
                .get(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
        // Could use PropertySourceUtils.getSubProperties() to extract common properties
        PropertySource<?> mailPropertySource = new MailPropertiesMapper(
                osEnvironmentPropertySource).map();
        System.out.println("Mail Properties Before"+mailPropertySource);
        if (mailPropertySource != null) {
            environment.getPropertySources().addAfter(
                    StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                    mailPropertySource);
        }
        System.out.println("Mail Properties "+mailPropertySource);
        System.out.println("======================= End =========================");
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    private static final class MailPropertiesMapper {


        private final PropertySource<?> propertySource;

        private MailPropertiesMapper(PropertySource<?> propertySource) {
            this.propertySource = propertySource;
        }

        private static String key(String suffix) {
            return "ACME_MAIL_" + suffix;
        }

        PropertySource map() {
            Map<String, Object> content = new LinkedHashMap<>();
            map(content, key("SMTP_STARTTLS_ENABLE"),
                    "spring.mail.properties.mail.smtp.starttls.enable");
            map(content, key("SMTP_CONNECTION_TIMEOUT"),
                    "spring.mail.properties.mail.smtp.connectiontimeout");
            // more mapping
            return !content.isEmpty() ? new MapPropertySource("mailProperties", content)
                    : null;
        }

        private void map(Map<String, Object> content, String key, String targetKey) {
            if (this.propertySource.containsProperty(key)) {
                content.put(targetKey, this.propertySource.getProperty(key));
            }
        }

    }

}
