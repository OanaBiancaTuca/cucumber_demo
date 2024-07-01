package org.example.cucumber_demo.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.example.cucumber_demo.context.TestContext;
import org.example.cucumber_demo.entity.Post;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Log4j2
public class StepDefinitions {

    @LocalServerPort
    private int port;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost";

    private final TestContext testContext = TestContext.CONTEXT;


    public Post getPostById(String id) {
        String url = baseUrl + ":" + port + "/post/" + Integer.parseInt(id);
        return restTemplate.getForObject(url, Post.class);
    }

    @Given("I can create a new post")
    public void iCanCreateAPost() {
        String url = baseUrl + ":" + port + "/post";
        Response response = testContext.getRequest().get(url);
        assertEquals(200, response.getStatusCode(), "Expected status code 200");

    }


    @When("I send a post to be created with title {string} and content {string}")
    public void iSendAPostToBeCreated(String title, String content) {
        String url = baseUrl + ":" + port + "/post";
        Post newPost = new Post(title, content);
        Response response = testContext.getRequest()
                .contentType("application/json")
                .body(newPost)
                .post(url);
        testContext.setResponse(response);
        Post createdPost = response.getBody().as(Post.class);
        testContext.set("createdPost", createdPost);
        log.info(createdPost+"###################");
        assertNotNull(createdPost, "The created post should not be null");
    }

    @Then("Response status should be {int}")
    public void responseStatusShouldBe(Integer statusCode) {
        Response response = testContext.getResponse();
        assertEquals(statusCode.intValue(), response.getStatusCode(), "Expected status code " + statusCode);
    }

    @Then("I should be able to see my newly created post")
    public void iWantToSeeNewlyPost() {
        Post createdPost = testContext.get("createdPost");
        String url = baseUrl + ":" + port + "/post/" + createdPost.getId();
        Response response = testContext.getRequest().get(url);
        Post myPost = response.getBody().as(Post.class);
        assertNotNull(myPost, "The retrieved post should not be null");
        assertEquals(createdPost.getTitle(), myPost.getTitle(), "The titles should match");
        assertEquals(createdPost.getContent(), myPost.getContent(), "The contents should match");
    }


    @Given("I can update the post with ID {string}")
    public void ICanUpdateAPost(String id) {
        Post post = getPostById(id);
        assertNotNull(post, "The retrieved post should not be null");
    }

    @When("I update the post with ID {string} to have the title {string} and the content {string}")
    public void iUpdateThePost(String id, String title, String content) {
        Post post = getPostById(id);
        String url = baseUrl + ":" + port + "/post/update/" + Integer.parseInt(id);
        post.setTitle(title);
        post.setContent(content);
        restTemplate.put(url, post);

    }

    @Then("The post with ID {string} should have the title {string} and the content {string}")
    public void postWithIdShouldHaveNewTitleAndContent(String id, String title, String content) {
        Post updatedPost = getPostById(id);
        assertNotNull(updatedPost, "The retrieved post should not be null");
        assertEquals(updatedPost.getTitle(), title, "The titles should match");
        assertEquals(updatedPost.getContent(), content, "The contents should match");

    }

    public ResponseEntity<Post[]> getAllPostsEndpoint() {
        String url = baseUrl + ":" + port + "/post";
        return restTemplate.getForEntity(url, Post[].class);
    }

    @Given("I can request to see all posts")
    public void i_can_request_to_see_all_posts() {
        assertNotNull(getAllPostsEndpoint().getBody(), "The list of posts should not be null");
    }


    @When("I request all posts")
    public void iRequestAllPost() {
        String url = baseUrl + ":" + port + "/post";
        ResponseEntity<Post[]> responseEntity = restTemplate.getForEntity(url, Post[].class);
    }

    @Then("The response status should be {int}")
    public void the_response_status_should_be(int expectedStatus) {
        String url = baseUrl + ":" + port + "/post";
        ResponseEntity<Post[]> responseEntity = restTemplate.getForEntity(url, Post[].class);
        Post[] posts = responseEntity.getBody();
        assertEquals(expectedStatus, responseEntity.getStatusCode().value());
    }


}
