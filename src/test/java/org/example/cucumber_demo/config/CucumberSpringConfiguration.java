package org.example.cucumber_demo.config;


import io.cucumber.spring.CucumberContextConfiguration;
import org.example.cucumber_demo.CucumberDemoApplication;
import org.example.cucumber_demo.context.TestContext;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
//configure the Spring context for Cucumber
@CucumberContextConfiguration
@SpringBootTest( classes = CucumberDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {
@Before
public void resetContext() {
    TestContext.CONTEXT.reset();
}
}
