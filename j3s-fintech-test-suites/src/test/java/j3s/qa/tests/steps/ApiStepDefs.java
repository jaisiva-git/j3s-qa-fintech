package j3s.qa.tests.steps;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ApiStepDefs {

    @Value("${tests.backendBaseUrl:http://localhost:5173}")
    private String BASE_URL;

    @Value("${tests.authToken:}")
    private String AUTH_TOKEN;

    private Response lastResponse;

    private final List<String> createdUserIds = new ArrayList<>();

    private Map<String,String> authHeader() {
        return (AUTH_TOKEN == null || AUTH_TOKEN.isBlank())
                ? Collections.emptyMap()
                : Map.of("Authorization", "Bearer " + AUTH_TOKEN);
    }

    @Given("the backend base URL is set")
    public void the_backend_base_url_is_set() {
        RestAssured.baseURI = BASE_URL;
    }

    @When("I create a user with name {string} email {string} type {string}")
    public void i_create_user(String name, String email, String type) {
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("accountType", type);

        lastResponse = given()
                .headers(authHeader())
                .contentType("application/json")
                .body(body)
                .when().post("/api/users")
                .then().extract().response();

        try {
            String id = lastResponse.jsonPath().getString("data.id");
            if (id != null) createdUserIds.add(id);
        } catch (Exception ignored) {}
    }

    @When("I request the list of users")
    public void i_request_users() {
        lastResponse = given()
                .headers(authHeader())
                .when().get("/api/users")
                .then().extract().response();
    }

    @Then("the response code should be {int}")
    public void the_response_code_should_be(Integer code) {
        Assertions.assertEquals(code.intValue(), lastResponse.statusCode(), "Unexpected HTTP status");
    }

    @Then("the response should contain field {string}")
    public void response_should_contain_field(String jsonPath) {
        Object v = lastResponse.jsonPath().get(jsonPath);
        Assertions.assertNotNull(v, "Expected field missing: " + jsonPath);
    }

    @Then("the list should contain at least {int} user")
    public void list_contains_users(Integer min) {
        List<?> l = lastResponse.jsonPath().getList("data");
        if (l == null) l = lastResponse.jsonPath().getList("$");
        assertThat(l.size(), greaterThanOrEqualTo(min));
    }

    @When("I create a transfer of {double} from the last user to the previous user")
    public void i_create_transfer_between_last_two(double amount) {
        Assertions.assertTrue(createdUserIds.size() >= 2, "Need two users created first");
        String sender = createdUserIds.get(createdUserIds.size()-1);
        String recipient = createdUserIds.get(createdUserIds.size()-2);

        Map<String,Object> body = new LinkedHashMap<>();
        body.put("userId", sender);
        body.put("amount", amount);
        body.put("type", "transfer");
        body.put("recipientId", recipient);

        lastResponse = given()
                .headers(authHeader())
                .contentType("application/json")
                .body(body)
                .when().post("/api/transactions")
                .then().extract().response();
    }

    @Then("the response message should contain {string}")
    public void message_contains(String snippet) {
        String body = lastResponse.asString();
        assertThat(body, containsString(snippet));
    }

    @When("I try to create a user with invalid email {string}")
    public void create_user_invalid_email(String email) {
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("name", "Bad");
        body.put("email", email);
        body.put("accountType", "standard");

        lastResponse = given()
                .headers(authHeader())
                .contentType("application/json")
                .body(body)
                .when().post("/api/users")
                .then().extract().response();
    }

    @Then("the response json path {string} should be true")
    public void the_response_json_path_should_be_true(String path) {
        //TODO: Cleanup -Jai
        java.lang.Boolean value = lastResponse.jsonPath().getBoolean(path);
        Assertions.assertTrue(Boolean.TRUE.equals(value),
                "Expected true at JSON path: " + path + " but got " + value);

    }
}
