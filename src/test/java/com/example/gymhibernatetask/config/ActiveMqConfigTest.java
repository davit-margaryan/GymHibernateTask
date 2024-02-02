package com.example.gymhibernatetask.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class ActiveMqConfigTest {

    @Mock
    private Environment environment;

    private ActiveMqConfig activeMqConfig;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(environment.getProperty("spring.activemq.broker-url")).thenReturn("broker-url");
        when(environment.getProperty("spring.activemq.user")).thenReturn("user");
        when(environment.getProperty("spring.activemq.password")).thenReturn("password");
        activeMqConfig = new ActiveMqConfig();
        activeMqConfig.setEnv(environment);
    }

    @Test
    public void shouldCreateAnActiveMqConnectionFactoryBean() {
        assertNotNull(activeMqConfig.connectionFactory());
    }

    @Test
    public void shouldCreateAJmsTemplateBean() {
        assertNotNull(activeMqConfig.jmsTemplate());
    }

    @Test
    public void shouldCreateAMappingJackson2MessageConverterBean() {
        assertNotNull(activeMqConfig.jacksonJmsMessageConverter());
    }
}