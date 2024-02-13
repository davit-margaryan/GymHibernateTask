package com.example.gymhibernatetask.integration;

import com.example.gymhibernatetask.GymHibernateTaskApplication;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@RunWith(Cucumber.class)
@ContextConfiguration
@SpringBootTest(classes = GymHibernateTaskApplication.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.example.gymhibernatetask"
)
public class CucumberIntegrationTest {

    @BeforeClass
    public static void setup() {
        System.setProperty("spring.profiles.active", "test");
    }
}