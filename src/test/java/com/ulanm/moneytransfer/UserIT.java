package com.ulanm.moneytransfer;

import com.jayway.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserIT {

    private static Map<String, String> userCache;

    @BeforeClass
    public static void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = Application.PORT_NUMBER;
        with()
                .post("/test")
                .then()
                .assertThat()
                .statusCode(201);
        userCache = new HashMap<>();
        with()
                .get("/user/all")
                .then()
                .extract()
                .body()
                .as(List.class)
                .forEach(item -> {
                    String id = ((Map<String, String>) item).get("id");
                    String name = ((Map<String, String>) item).get("name");
                    userCache.put(name, id);
                });
    }

    @AfterClass
    public static void resetRestAssured() {
        RestAssured.reset();
    }

    /**
     * Positive case - user created
     */
    @Test
    public void testCreateUserPass() {
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"name\": \"test_user_create\"\n" +
                        "}")
                .when()
                .post("/user/create")
                .then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("name", equalTo("test_user_create"));
    }

    /**
     * Negative case - malformed JSON
     */
    @Test
    public void testCreateUserFail1() {
        given()
                .header("Content-Type", "application/json")
                .body("lol")
                .when()
                .post("/user/create")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Wrong JSON format.");
    }

    /**
     * Negative case - "name" is empty
     */
    @Test
    public void testCreateUserFail2() {
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"name\": \"\"\n" +
                        "}")
                .when()
                .post("/user/create")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Name cannot be empty or consist only of whitespaces.");
    }

    /**
     * Positive case - user list retrieved
     */
    @Test
    public void testGetAllUsers() {
        when()
                .get("/user/all")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("find { it.name == 'John Doe' }", allOf(
                        hasKey("id"),
                        hasKey("creationDateTime")
                ));
    }

    /**
     * Positive case - user info retrieved
     */
    @Test
    public void testGetUserPass() {
        String id = userCache.get("test_user_info");
        when()
                .get("/user/info/" + id)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", allOf(
                        hasKey("id"),
                        hasKey("name"),
                        hasKey("creationDateTime")
                ));
    }

    /**
     * Negative case - wrong ID
     */
    @Test
    public void testGetUserFail() {
        final String wrongId = "0";
        when()
                .get("/user/info/" + wrongId)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No user found with ID: " + wrongId);
    }

    /**
     * Positive case - user updated
     */
    @Test
    public void testUpdateUserPass() {
        final String newName = "Luke Skywalker";
        String id = userCache.get("test_user_update");
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"name\": \"" + newName + "\"\n" +
                        "}")
                .when()
                .put("/user/edit/" + id)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", allOf(
                        hasKey("id"),
                        hasEntry("name", newName),
                        hasKey("creationDateTime")
                ));
    }

    /**
     * Negative case - malformed JSON
     */
    @Test
    public void testUpdateUserFail1() {
        String id = userCache.get("test_user_update");
        given()
                .header("Content-Type", "application/json")
                .body("I am your father!")
                .when()
                .put("/user/edit/" + id)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Wrong JSON format.");
    }

    /**
     * Negative case - empty ID
     */
    @Test
    public void testUpdateUserFail2() {
        final String newName = "Luke Skywalker";
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"name\": \"" + newName + "\"\n" +
                        "}")
                .when()
                .put("/user/edit/")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 User ID cannot be empty.");
    }

    /**
     * Negative case - non-existing user
     */
    @Test
    public void testUpdateUserFail3() {
        final String newName = "Luke Skywalker";
        final String wrongId = "no-such-id";
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"name\": \"" + newName + "\"\n" +
                        "}")
                .when()
                .put("/user/edit/" + wrongId)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No user found with ID: " + wrongId);
    }

    /**
     * Negative case - empty name
     */
    @Test
    public void testUpdateUserFail4() {
        String id = userCache.get("test_user_update");
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"name\": \"\"\n" +
                        "}")
                .when()
                .put("/user/edit/" + id)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Name cannot be empty or consist only of whitespaces.");
    }

    /**
     * Positive case - user deleted
     */
    @Test
    public void testDeleteUserPass() {
        String id = userCache.get("test_user_delete");
        when()
                .delete("/user/delete/" + id)
                .then()
                .assertThat()
                .statusCode(200);
    }

    /**
     * Negative case - empty ID
     */
    @Test
    public void testDeleteUserFail1() {
        when()
                .delete("/user/delete/")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 User ID cannot be empty.");
    }

    /**
     * Negative case - non-existing user
     */
    @Test
    public void testDeleteUserFail2() {
        when()
                .delete("/user/delete/no-such-id")
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No user found with ID: no-such-id");
    }

    /**
     * Positive case - user accounts retrieved
     */
    @Test
    public void testGetUserAccountsPass() {
        final String id = userCache.get("test_user_info");
        when()
                .get("/user/accounts/" + id)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", hasItem(Matchers.allOf(
                        hasKey("id"),
                        hasKey("owner"),
                        hasKey("name"),
                        hasKey("creationDateTime"),
                        hasKey("balance"),
                        hasKey("currency"),
                        hasKey("active")
                )));
    }

    /**
     * Negative case - empty ID
     */
    @Test
    public void testGetUserAccountsFail1() {
        when()
                .get("/user/accounts/")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 User ID cannot be empty.");
    }

    /**
     * Negative case - non-existing user
     */
    @Test
    public void testGetUserAccountsFail2() {
        final String wrongId = "no-such-id";
        when()
                .get("/user/accounts/" + wrongId)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No user found with ID: " + wrongId);
    }

    /**
     * Positive case - user account created
     */
    @Test
    public void testCreateUserAccountPass() {
        final String name = "test_user_info";
        final String id = userCache.get(name);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"" + id + "\",\n" +
                        "    \"name\": \"new_account\",\n" +
                        "    \"balance\": \"1500.00\",\n" +
                        "    \"currency\": \"USD\",\n" +
                        "    \"active\": true\n" +
                        "}")
                .when()
                .post("/user/create-account")
                .then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("$", allOf(
                        hasKey("id"),
                        hasKey("owner"),
                        hasEntry("name", "new_account"),
                        hasKey("creationDateTime"),
                        hasEntry("balance", "1500.00"),
                        hasEntry("currency", "USD"),
                        hasKey("active")
                ))
                .and()
                .body("active", equalTo(true))
                .and()
                .body("owner", allOf(
                        hasEntry("id", id),
                        hasEntry("name", name),
                        hasKey("creationDateTime")
                ));
    }

    /**
     * Negative case - empty owner ID
     */
    @Test
    public void testCreateUserAccountFail1() {
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"\",\n" +
                        "    \"name\": \"new_account\",\n" +
                        "    \"balance\": \"1500.00\",\n" +
                        "    \"currency\": \"USD\",\n" +
                        "    \"active\": true\n" +
                        "}")
                .when()
                .post("/user/create-account")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Owner ID cannot be empty or consist only of whitespaces.");
    }

    /**
     * Negative case - empty name
     */
    @Test
    public void testCreateUserAccountFail2() {
        final String id = userCache.get("test_user_info");
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"" + id + "\",\n" +
                        "    \"name\": \"\",\n" +
                        "    \"balance\": \"1500.00\",\n" +
                        "    \"currency\": \"USD\",\n" +
                        "    \"active\": true\n" +
                        "}")
                .when()
                .post("/user/create-account")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Name cannot be empty or consist only of whitespaces.");
    }

    /**
     * Negative case - empty currency
     */
    @Test
    public void testCreateUserAccountFail3() {
        final String id = userCache.get("test_user_info");
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"" + id + "\",\n" +
                        "    \"name\": \"new_account\",\n" +
                        "    \"balance\": \"1500.00\",\n" +
                        "    \"currency\": \"\",\n" +
                        "    \"active\": true\n" +
                        "}")
                .when()
                .post("/user/create-account")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Currency cannot be empty or consist only of whitespaces.");
    }

    /**
     * Negative case - malformed JSON
     */
    @Test
    public void testCreateUserAccountFail4() {
        given()
                .header("Content-Type", "application/json")
                .body("{malformed_json}")
                .when()
                .post("/user/create-account")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Wrong JSON format.");
    }

    /**
     * Negative case - non-existing user
     */
    @Test
    public void testCreateUserAccountFail5() {
        final String wrongId = "no-such-id";
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"" + wrongId + "\",\n" +
                        "    \"name\": \"new_account\",\n" +
                        "    \"balance\": \"1500.00\",\n" +
                        "    \"currency\": \"\",\n" +
                        "    \"active\": true\n" +
                        "}")
                .when()
                .post("/user/create-account")
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No user found with ID: " + wrongId);
    }

    /**
     * Negative case - wrong currency code
     */
    @Test
    public void testCreateUserAccountFail6() {
        final String id = userCache.get("test_user_info");
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"" + id + "\",\n" +
                        "    \"name\": \"new_account\",\n" +
                        "    \"balance\": \"1500.00\",\n" +
                        "    \"currency\": \"123\",\n" +
                        "    \"active\": true\n" +
                        "}")
                .when()
                .post("/user/create-account")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Wrong currency code.");
    }

    /**
     * Negative case - non-numeric balance format
     */
    @Test
    public void testCreateUserAccountFail7() {
        final String id = userCache.get("test_user_info");
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"" + id + "\",\n" +
                        "    \"name\": \"new_account\",\n" +
                        "    \"balance\": \"ABCDE\",\n" +
                        "    \"currency\": \"\",\n" +
                        "    \"active\": true\n" +
                        "}")
                .when()
                .post("/user/create-account")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Wrong balance format.");
    }

    /**
     * Positive case - users found
     */
    @Test
    public void testFindUsersPass() {
        given()
                .param("name", "test_user")
                .when()
                .get("/user/find")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", hasItem(allOf(
                        hasKey("id"),
                        hasKey("name"),
                        hasKey("creationDateTime")
                )))
                .and()
                .body("$", everyItem(hasEntry(equalTo("name"), containsString("test_user"))));
    }

    /**
     * Negative case - empty name
     */
    @Test
    public void testFindUsersFail() {
        when()
                .get("/user/find")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Name cannot be empty or consist only of whitespaces.");
    }

}
