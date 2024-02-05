package config;

import com.example.reportmicro.config.ActiveMqConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class ActiveMqConfigTest {

    @Mock
    private Environment env;

    private ActiveMqConfig config;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(env.getProperty("spring.activemq.broker-url")).thenReturn("broker-url");
        when(env.getProperty("spring.activemq.user")).thenReturn("user");
        when(env.getProperty("spring.activemq.password")).thenReturn("password");
        config = new ActiveMqConfig();
        config.setEnv(env);
    }

    @Test
    public void shouldCreateAnActiveMqConnectionFactoryBean() {
        assertNotNull(config.connectionFactory());
    }

    @Test
    public void shouldCreateAJmsListenerContainerFactoryBean() {
        assertNotNull(config.jmsListenerContainerFactory());
    }

    @Test
    public void shouldCreateAMappingJackson2MessageConverterBean() {
        assertNotNull(config.jacksonJmsMessageConverter());
    }

    @Test
    public void shouldCreateAnErrorHandlerBean() {
        assertNotNull(config.errorHandler());
    }

    @Test
    public void shouldCreateAJmsTemplateBean() {
        assertNotNull(config.jmsTemplate());
    }
}