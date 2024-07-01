package org.example.cucumber_demo.context;


import static io.restassured.RestAssured.given;
import static java.lang.ThreadLocal.withInitial;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


import java.util.HashMap;
import java.util.Map;

//TestContext has all the code in place to store HTTP Request Payload, HTTP Request (Headers & Params), HTTP Response Received.
public enum TestContext {

    CONTEXT;

    private static final String PAYLOAD = "PAYLOAD";
    private static final String REQUEST = "REQUEST";
    private static final String RESPONSE = "RESPONSE";
    private final ThreadLocal<Map<String, Object>> testContexts = withInitial(HashMap::new);
//    test context uses ThreadLocal and there are methods to set and get HTTP Request, Payload and HTTP Response.

    public <T> T get(String name) {
        return (T) testContexts.get()
                .get(name);
    }

    public <T> T set(String name, T object) {
        testContexts.get()
                .put(name, object);
        return object;
    }

    public RequestSpecification getRequest() {
        if (null == get(REQUEST)) {
            set(REQUEST, given().log()
                    .all());
        }

        return get(REQUEST);
    }

    public Response getResponse() {
        return get(RESPONSE);
    }

    public Response setResponse(Response response) {
        return set(RESPONSE, response);
    }

    public Object getPayload() {
        return get(PAYLOAD);
    }

    public <T> T getPayload(Class<T> clazz) {
        return clazz.cast(get(PAYLOAD));
    }

    public <T> void setPayload(T object) {
        set(PAYLOAD, object);
    }

    public void reset() {
        testContexts.get()
                .clear();
    }
}