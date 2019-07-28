package com.ulanm.moneytransfer;

import com.jayway.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class AccountIT {

    private static List<String> userCache;
    private static List<String> accountCache;

    @BeforeClass
    public static void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = Application.PORT_NUMBER;
        with()
                .post("/test")
                .then()
                .assertThat()
                .statusCode(201);
        userCache = new ArrayList<>();
        accountCache = new ArrayList<>();
        with()
                .get("/user/all")
                .then()
                .extract()
                .body()
                .as(List.class)
                .forEach(item -> {
                    String id = ((Map<String, String>) item).get("id");
                    userCache.add(id);
                    with()
                            .get("/user/accounts/" + id)
                            .then()
                            .extract()
                            .body()
                            .as(List.class)
                            .forEach(account -> accountCache.add((String) ((Map) account).get("id")));
                });
    }

    @AfterClass
    public static void resetConfigureRestAssured() {
        RestAssured.reset();
    }

    /**
     * Positive case - account information retrieved
     */
    @Test
    public void testGetAccountPass() {
        final String id = accountCache.get(0);
        with()
                .get("/account/info/" + id)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", allOf(
                        hasKey("owner"),
                        hasKey("name"),
                        hasKey("creationDateTime"),
                        hasKey("balance"),
                        hasKey("currency"),
                        hasKey("active"),
                        hasEntry("id", id)
                ));
    }

    /**
     * Positive case - account updated
     */
    @Test
    public void testUpdateAccountPass() {
        final String id = accountCache.get(0);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"" + userCache.get(0) + "\",\n" +
                        "    \"name\": \"updated_account\",\n" +
                        "    \"balance\": \"500.00\",\n" +
                        "    \"currency\": \"USD\",\n" +
                        "    \"active\": true\n" +
                        "}")
                .when()
                .put("/account/edit/" + id)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", allOf(
                        hasKey("owner"),
                        hasEntry("name", "updated_account"),
                        hasKey("creationDateTime"),
                        hasEntry("balance", "500.00"),
                        hasEntry("currency", "USD"),
                        hasKey("active"),
                        hasEntry("id", id)
                ))
                .and()
                .body("active", equalTo(true))
                .and()
                .body("owner", hasEntry("id", userCache.get(0)));
    }

    /**
     * Positive case - account deleted
     */
    @Test
    public void testDeleteAccountPass() {
        final String id = accountCache.get(accountCache.size() - 1);
        with()
                .delete("/account/delete/" + id)
                .then()
                .assertThat()
                .statusCode(200);
        accountCache.remove(id);
    }

    /**
     * Positive case - account activated
     */
    @Test
    public void testActivateAccountPass() {
        final String id = accountCache.get(0);
        with()
                .put("/account/activate/" + id)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", allOf(
                        hasKey("owner"),
                        hasKey("name"),
                        hasKey("creationDateTime"),
                        hasKey("balance"),
                        hasKey("currency"),
                        hasKey("active"),
                        hasEntry("id", id)
                ))
                .and()
                .body("active", equalTo(true));
    }

    /**
     * Positive case - account deactivated
     */
    @Test
    public void testDeactivateAccountPass() {
        final String id = accountCache.get(0);
        with()
                .put("/account/deactivate/" + id)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", allOf(
                        hasKey("owner"),
                        hasKey("name"),
                        hasKey("creationDateTime"),
                        hasKey("balance"),
                        hasKey("currency"),
                        hasKey("active"),
                        hasEntry("id", id)
                ))
                .and()
                .body("active", equalTo(false));
        accountCache.remove(id);
    }

    /**
     * Positive case - account transactions retrieved
     */
    @Test
    public void testGetAccountTransactionsPass() {
        final String id = accountCache.get(0);
        with()
                .get("/account/transactions/" + id)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", everyItem(allOf(
                        hasKey("id"),
                        hasKey("sourceAccount"),
                        hasKey("destinationAccount"),
                        hasKey("amount"),
                        hasKey("currency"),
                        hasKey("comment"),
                        hasKey("status"),
                        hasKey("creationDateTime")
                )))
                .and()
                .body("$", everyItem(anyOf(
                        hasEntry(equalTo("sourceAccount"), hasEntry("id", id)),
                        hasEntry(equalTo("destinationAccount"), hasEntry("id", id))
                )));
    }

    /**
     * Positive case - money deposited
     */
    @Test
    public void testDepositPass() {
        final String id = accountCache.get(0);
        final String toAdd = "500.00";
        DecimalFormat df = new DecimalFormat("#.00");
        String balanceStr = with()
                .get("/account/info/" + id)
                .then()
                .extract()
                .body()
                .jsonPath()
                .get("balance");
        BigDecimal balance = new BigDecimal(balanceStr);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"targetAccountId\": \"" + id + "\",\n" +
                        "    \"amount\": \"" + toAdd + "\"\n" +
                        "}")
                .when()
                .post("/account/deposit")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", allOf(
                        hasKey("id"),
                        hasKey("sourceAccount"),
                        hasKey("destinationAccount"),
                        hasEntry("amount", toAdd),
                        hasKey("currency"),
                        hasEntry("comment", "Deposit"),
                        hasEntry("status", "EXECUTED"),
                        hasKey("creationDateTime"),
                        hasKey("executionDateTime")
                ))
                .and()
                .body("destinationAccount", hasEntry(equalTo("id"), equalTo(id)));
        with()
                .get("/account/info/" + id)
                .then()
                .assertThat()
                .body("balance", equalTo(df.format(balance.add(new BigDecimal(toAdd)))));
    }

    /**
     * Positive case - money withdrawn
     */
    @Test
    public void testWithdrawPass() {
        final String id = accountCache.get(0);
        final String toSubtract = "500.00";
        DecimalFormat df = new DecimalFormat("#.00");
        String balanceStr = with()
                .get("/account/info/" + id)
                .then()
                .extract()
                .body()
                .jsonPath()
                .get("balance");
        BigDecimal balance = new BigDecimal(balanceStr);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"targetAccountId\": \"" + id + "\",\n" +
                        "    \"amount\": \"" + toSubtract + "\"\n" +
                        "}")
                .when()
                .post("/account/withdraw")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", allOf(
                        hasKey("id"),
                        hasKey("sourceAccount"),
                        hasKey("destinationAccount"),
                        hasEntry("amount", toSubtract),
                        hasKey("currency"),
                        hasEntry("comment", "Withdrawal"),
                        hasEntry("status", "EXECUTED"),
                        hasKey("creationDateTime"),
                        hasKey("executionDateTime")
                ))
                .and()
                .body("sourceAccount", hasEntry(equalTo("id"), equalTo(id)));
        with()
                .get("/account/info/" + id)
                .then()
                .assertThat()
                .body("balance", equalTo(df.format(balance.subtract(new BigDecimal(toSubtract)))));
    }

    /**
     * Positive case - money transferred
     */
    @Test
    public void testTransferPass() {
        final String id1 = accountCache.get(0);
        final String id2 = accountCache.get(1);
        final String toTransfer = "500.00";
        DecimalFormat df = new DecimalFormat("#.00");
        String balanceStr = with()
                .get("/account/info/" + id1)
                .then()
                .extract()
                .body()
                .jsonPath()
                .get("balance");
        BigDecimal balance = new BigDecimal(balanceStr);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"targetAccountId\": \"" + id1 + "\",\n" +
                        "    \"amount\": \"" + toTransfer + "\"\n" +
                        "}")
                .when()
                .post("/account/withdraw")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", allOf(
                        hasKey("id"),
                        hasKey("sourceAccount"),
                        hasKey("destinationAccount"),
                        hasEntry("amount", toTransfer),
                        hasKey("currency"),
                        hasEntry("comment", "Withdrawal"),
                        hasEntry("status", "EXECUTED"),
                        hasKey("creationDateTime"),
                        hasKey("executionDateTime")
                ))
                .and()
                .body("sourceAccount", hasEntry(equalTo("id"), equalTo(id1)));
        with()
                .get("/account/info/" + id1)
                .then()
                .assertThat()
                .body("balance", equalTo(df.format(balance.subtract(new BigDecimal(toTransfer)))));
    }

    /**
     * Negative case - non-existing account
     */
    @Test
    public void testGetAccountFail() {
        final String id = "no-such-id";
        with()
                .get("/account/info/" + id)
                .then()
                .assertThat()
                .statusCode(404)
                .statusLine("HTTP/1.1 404 No account found with ID: " + id);
    }

    /**
     * Negative case - malformed JSON
     */
    @Test
    public void testUpdateAccountFail1() {
        final String id = accountCache.get(0);
        given()
                .header("Content-Type", "application/json")
                .body("{broken JSON}")
                .when()
                .put("/account/edit/" + id)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Wrong JSON format.");
    }

    /**
     * Negative case - non-existing account
     */
    @Test
    public void testUpdateAccountFail2() {
        final String accountId = "no-such-id";
        final String userId = userCache.get(0);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"" + userId + "\",\n" +
                        "    \"name\": \"updated_account\",\n" +
                        "    \"balance\": \"500.00\",\n" +
                        "    \"currency\": \"USD\",\n" +
                        "    \"active\": true\n" +
                        "}")
                .when()
                .put("/account/edit/" + accountId)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No account found with ID: " + accountId);
    }

    /**
     * Negative case - non-existing user
     */
    @Test
    public void testUpdateAccountFail3() {
        final String accountId = accountCache.get(0);
        final String userId = "no-such-id";
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"" + userId + "\",\n" +
                        "    \"name\": \"updated_account\",\n" +
                        "    \"balance\": \"500.00\",\n" +
                        "    \"currency\": \"USD\",\n" +
                        "    \"active\": true\n" +
                        "}")
                .when()
                .put("/account/edit/" + accountId)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No user found with ID: " + userId);
    }

    /**
     * Negative case - wrong currency code
     */
    @Test
    public void testUpdateAccountFail4() {
        final String id = accountCache.get(0);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"" + userCache.get(0) + "\",\n" +
                        "    \"name\": \"updated_account\",\n" +
                        "    \"balance\": \"500.00\",\n" +
                        "    \"currency\": \"123\",\n" +
                        "    \"active\": true\n" +
                        "}")
                .when()
                .put("/account/edit/" + id)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Wrong currency code.");
    }

    /**
     * Negative case - wrong number format
     */
    @Test
    public void testUpdateAccountFail5() {
        final String id = accountCache.get(0);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"" + userCache.get(0) + "\",\n" +
                        "    \"name\": \"updated_account\",\n" +
                        "    \"balance\": \"ABCDEF\",\n" +
                        "    \"currency\": \"USD\",\n" +
                        "    \"active\": true\n" +
                        "}")
                .when()
                .put("/account/edit/" + id)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Wrong balance format.");
    }

    /**
     * Negative case - no active flag
     */
    @Test
    public void testUpdateAccountFail6() {
        final String id = accountCache.get(0);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"ownerId\": \"" + userCache.get(0) + "\",\n" +
                        "    \"name\": \"updated_account\",\n" +
                        "    \"balance\": \"500.00\",\n" +
                        "    \"currency\": \"USD\"\n" +
                        "}")
                .when()
                .put("/account/edit/" + id)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Active flag cannot be null.");
    }

    /**
     * Negative case - non-existing account
     */
    @Test
    public void testDeleteAccountFail() {
        final String id = "no-such-id";
        with()
                .delete("/account/delete/" + id)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No account found with ID: " + id);
    }

    /**
     * Negative case - non-existing account
     */
    @Test
    public void testActivateAccountFail() {
        final String id = "no-such-id";
        with()
                .put("/account/activate/" + id)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No account found with ID: " + id);
    }

    /**
     * Negative case - non-existing account
     */
    @Test
    public void testDeactivateAccountFail() {
        final String id = "no-such-id";
        with()
                .put("/account/deactivate/" + id)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No account found with ID: " + id);
    }

    /**
     * Negative case - non-existing account
     */
    @Test
    public void testGetAccountTransactionsFail() {
        final String id = "no-such-id";
        with()
                .get("/account/transactions/" + id)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No account found with ID: " + id);
    }

    /**
     * Negative case - non-existing account
     */
    @Test
    public void testDepositFail1() {
        final String id = "no-such-id";
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"targetAccountId\": \"" + id + "\",\n" +
                        "    \"amount\": \"500.00\"\n" +
                        "}")
                .when()
                .post("/account/deposit")
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No account found with ID: " + id);
    }

    /**
     * Negative case - wrong number format
     */
    @Test
    public void testDepositFail2() {
        final String id = accountCache.get(0);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"targetAccountId\": \"" + id + "\",\n" +
                        "    \"amount\": \"not-a-number\"\n" +
                        "}")
                .when()
                .post("/account/deposit")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Wrong amount format.");
    }

    /**
     * Negative case - negative amount
     */
    @Test
    public void testDepositFail3() {
        final String id = accountCache.get(0);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"targetAccountId\": \"" + id + "\",\n" +
                        "    \"amount\": \"-500.00\"\n" +
                        "}")
                .when()
                .post("/account/deposit")
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .statusLine("HTTP/1.1 403 Amount cannot be less or equal to 0.");
    }

    /**
     * Negative case - non-existing account
     */
    @Test
    public void testWithdrawFail1() {
        final String id = "no-such-id";
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"targetAccountId\": \"" + id + "\",\n" +
                        "    \"amount\": \"500.00\"\n" +
                        "}")
                .when()
                .post("/account/withdraw")
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No account found with ID: " + id);
    }

    /**
     * Negative case - wrong number format
     */
    @Test
    public void testWithdrawFail2() {
        final String id = accountCache.get(0);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"targetAccountId\": \"" + id + "\",\n" +
                        "    \"amount\": \"not-a-number\"\n" +
                        "}")
                .when()
                .post("/account/withdraw")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Wrong amount format.");
    }

    /**
     * Negative case - negative amount
     */
    @Test
    public void testWithdrawFail3() {
        final String id = accountCache.get(0);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"targetAccountId\": \"" + id + "\",\n" +
                        "    \"amount\": \"-500.00\"\n" +
                        "}")
                .when()
                .post("/account/withdraw")
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .statusLine("HTTP/1.1 403 Amount cannot be less or equal to 0.");
    }

    /**
     * Negative case - insufficient funds
     */
    @Test
    public void testWithdrawFail4() {
        final String id = accountCache.get(0);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"targetAccountId\": \"" + id + "\",\n" +
                        "    \"amount\": \"50000.00\"\n" +
                        "}")
                .when()
                .post("/account/withdraw")
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .statusLine("HTTP/1.1 403 Insufficient funds.");
    }

    /**
     * Negative case - non-existing source account
     */
    @Test
    public void testTransferFail1() {
        final String sourceId = "no-such-id";
        final String destinationId = accountCache.get(0);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"sourceAccountId\": \"" + sourceId + "\",\n" +
                        "    \"destinationAccountId\": \"" + destinationId + "\",\n" +
                        "    \"comment\": \"a test transfer\",\n" +
                        "    \"currency\": \"USD\",\n" +
                        "    \"amount\": \"100.00\"\n" +
                        "}")
                .when()
                .post("/account/transfer")
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No account found with ID: " + sourceId);
    }
    /**
     * Negative case - non-existing destination account
     */
    @Test
    public void testTransferFail2() {
        final String sourceId = accountCache.get(0);
        final String destinationId = "no-such-id";
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"sourceAccountId\": \"" + sourceId + "\",\n" +
                        "    \"destinationAccountId\": \"" + destinationId + "\",\n" +
                        "    \"comment\": \"a test transfer\",\n" +
                        "    \"currency\": \"USD\",\n" +
                        "    \"amount\": \"100.00\"\n" +
                        "}")
                .when()
                .post("/account/transfer")
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No account found with ID: " + destinationId);
    }

    /**
     * Negative case - currency mismatch
     */
    @Test
    public void testTransferFail3() {
        final String sourceId = accountCache.get(0);
        final String destinationId = accountCache.get(1);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"sourceAccountId\": \"" + sourceId + "\",\n" +
                        "    \"destinationAccountId\": \"" + destinationId + "\",\n" +
                        "    \"comment\": \"a test transfer\",\n" +
                        "    \"currency\": \"EUR\",\n" +
                        "    \"amount\": \"100.00\"\n" +
                        "}")
                .when()
                .post("/account/transfer")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .statusLine("HTTP/1.1 400 Currencies do not match. Auto-conversion is not available.");
    }

    /**
     * Negative case - negative amount
     */
    @Test
    public void testTransferFail4() {
        final String sourceId = accountCache.get(0);
        final String destinationId = accountCache.get(1);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"sourceAccountId\": \"" + sourceId + "\",\n" +
                        "    \"destinationAccountId\": \"" + destinationId + "\",\n" +
                        "    \"comment\": \"a test transfer\",\n" +
                        "    \"currency\": \"USD\",\n" +
                        "    \"amount\": \"-100.00\"\n" +
                        "}")
                .when()
                .post("/account/transfer")
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .statusLine("HTTP/1.1 403 Amount cannot be less or equal to 0.");
    }

    /**
     * Negative case - insufficient funds
     */
    @Test
    public void testTransferFail5() {
        final String sourceId = accountCache.get(0);
        final String destinationId = accountCache.get(1);
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"sourceAccountId\": \"" + sourceId + "\",\n" +
                        "    \"destinationAccountId\": \"" + destinationId + "\",\n" +
                        "    \"comment\": \"a test transfer\",\n" +
                        "    \"currency\": \"USD\",\n" +
                        "    \"amount\": \"50000.00\"\n" +
                        "}")
                .when()
                .post("/account/transfer")
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .statusLine("HTTP/1.1 403 Insufficient funds.");
    }

}
