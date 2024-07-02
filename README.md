<dependencies>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-java</artifactId>
        <version>6.10.4</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-spring</artifactId>
        <version>6.10.4</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-junit</artifactId>
        <version>6.10.4</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.github.tomakehurst</groupId>
        <artifactId>wiremock-jre8</artifactId>
        <version>2.27.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>3.9.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
Pasul 2: Crearea fișierului de feature
Creează un fișier de feature pentru a defini scenariile de testare Cucumber. De exemplu, src/test/resources/features/unsubscribe.feature:

gherkin
Copiază codul
Feature: Unsubscribe Wero Customer

  Scenario: Successfully unsubscribe a Wero customer
    Given the external API is available and returns a valid response
    When I unsubscribe the Wero customer with id "123"
    Then I should receive a success response

  Scenario: Unsubscribe a Wero customer with invalid id
    Given the external API returns a 404 Not Found
    When I unsubscribe the Wero customer with id "invalid-id"
    Then I should receive a not found error
Pasul 3: Configurarea step definitions
Creează clasele de step definitions pentru a implementa logica testelor. De exemplu, src/test/java/com/example/test/UnsubscribeStepDefinitions.java:

java
Copiază codul
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.service.LcmUnsubscribeWeroService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

@SpringBootTest
public class UnsubscribeStepDefinitions {

    private WireMockServer wireMockServer;

    @Mock
    private PermissionApiClient permissionApiClient;

    @InjectMocks
    @Autowired
    private LcmUnsubscribeWeroService unsubscribeWeroService;

    private String response;
    private Exception exception;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        wireMockServer = new WireMockServer(wireMockConfig().httpsPort(9999));
        wireMockServer.start();
        configureFor("localhost", 9999);
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    private Map<String, Object> getResponseMapping(String key) throws Exception {
        File file = ResourceUtils.getFile("classpath:responses.json");
        String content = new String(Files.readAllBytes(file.toPath()));
        Map<String, Object> responses = new ObjectMapper().readValue(content, new TypeReference<Map<String, Object>>() {});
        return (Map<String, Object>) responses.get(key);
    }

    @Given("the external API is available and returns a valid response")
    public void the_external_api_is_available_and_returns_a_valid_response() throws Exception {
        Map<String, Object> validResponse = getResponseMapping("valid_response");
        stubFor(get(urlPathEqualTo("/v4/permissions/involved-parties/123"))
                .willReturn(aResponse()
                        .withStatus((Integer) validResponse.get("status"))
                        .withHeader("Content-Type", (String) ((Map<String, Object>) validResponse.get("headers")).get("Content-Type"))
                        .withBody((String) validResponse.get("body"))));
    }

    @Given("the external API returns a 404 Not Found")
    public void the_external_api_returns_a_404_not_found() throws Exception {
        Map<String, Object> notFoundResponse = getResponseMapping("not_found_response");
        stubFor(get(urlPathEqualTo("/v4/permissions/involved-parties/invalid-id"))
                .willReturn(aResponse()
                        .withStatus((Integer) notFoundResponse.get("status"))
                        .withHeader("Content-Type", (String) ((Map<String, Object>) notFoundResponse.get("headers")).get("Content-Type"))
                        .withBody((String) notFoundResponse.get("body"))));
    }

    @When("I unsubscribe the Wero customer with id {string}")
    public void i_unsubscribe_the_wero_customer_with_id(String accountId) {
        try {
            response = unsubscribeWeroService.unsubscribe(accountId, "valid-token").get();
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("I should receive a success response")
    public void i_should_receive_a_success_response() {
        assertNotNull(response);
        assertEquals("response", response);
    }

    @Then("I should receive a not found error")
    public void i_should_receive_a_not_found_error() {
        assertNotNull(exception);
        assertTrue(exception.getCause() instanceof HttpClientErrorException.NotFound);
    }
}
Pasul 4: Configurarea clasei de test runner
Creează o clasă de test runner pentru Cucumber. De exemplu, src/test/java/com/example/test/CucumberTest.java:




Pasul 4: Configurarea clasei de test runner
Creează o clasă de test runner pentru Cucumber. De exemplu, src/test/java/com/example/test/CucumberTest.java:

java
Copiază codul
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.ConfigurationParameter;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.test")
public class CucumberTest {
}
Pasul 5: Configurarea serviciului de producție
Vom crea un serviciu simplu care face cereri către un API extern folosind PermissionApiClient. Asigură-te că acest serviciu folosește URL-ul corect.

PermissionApiClient.java
Asigură-te că PermissionApiClient este configurat corect.

java
Copiază codul
public interface PermissionApiClient {
    CompletableFuture<String> permissionGetInvolvedParties(String accountId, String accessToken);
}
Pasul 6: Implementarea serviciului LcmUnsubscribeWeroService
Implementăm serviciul de producție care utilizează PermissionApiClient.

LcmUnsubscribeWeroService.java
java
Copiază codul
import org.springframework.stereotype.Service;

@Service
public class LcmUnsubscribeWeroService {

    private final PermissionApiClient permissionApiClient;

    public LcmUnsubscribeWeroService(PermissionApiClient permissionApiClient) {
        this.permissionApiClient = permissionApiClient;
    }

    public CompletableFuture<String> unsubscribe(String accountId, String accessToken) {
        return permissionApiClient.permissionGetInvolvedParties(accountId, accessToken);
    }
}
Pasul 7: Configurarea fișierului .json pentru răspunsurile simulate
Asigură-te că fișierul responses.json este corect configurat și plasat în src/test/resources.

src/test/resources/responses.json
json
Copiază codul
{
  "valid_response": {
    "status": 200,
    "body": "\"response\"",
    "headers": {
      "Content-Type": "application/json"
    }
  },
  "not_found_response": {
    "status": 404,
    "body": "\"Not Found\"",
    "headers": {
      "Content-Type": "application/json"
    }
  }
}



