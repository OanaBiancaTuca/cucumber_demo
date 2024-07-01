package org.example.cucumber_demo.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

// Specify that this class will use the Cucumber runner to run the tests
@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "json:target/cucumber.json"},
        features = "classpath:features",   // Specify the location of the .feature files
        glue = {"org.example.cucumber_demo.steps", "org.example.cucumber_demo.config"} // Specify the package where the step definition classes are located and where configuration is located
)
public class CucumberRunnerTests {


}
