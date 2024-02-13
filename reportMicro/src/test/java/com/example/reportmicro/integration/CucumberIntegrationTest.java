package com.example.reportmicro.integration;

import com.example.reportmicro.ReportMicroApplication;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@RunWith(Cucumber.class)
@ContextConfiguration
@SpringBootTest(classes = ReportMicroApplication.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.example.reportmicro"
)
public class CucumberIntegrationTest {

    @BeforeClass
    public static void setup() {
        System.setProperty("spring.profiles.active", "test");
    }
}