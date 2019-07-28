package com.ulanm.moneytransfer;

import com.jayway.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.with;

public class TransactionIT {

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
        accountCache = new ArrayList<>();
        with()
                .get("/user/all")
                .then()
                .extract()
                .body()
                .as(List.class)
                .forEach(item -> {
                    String id = ((Map<String, String>) item).get("id");
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
     *  Positive case - transaction retrieved
     */
    @Test
    public void testGetTransactionPass() {
        final String accountId = accountCache.get(0);
        List transactionList = with()
                .get("/account/transactions/" + accountId)
                .then()
                .extract()
                .body()
                .as(List.class);
        final String transactionId = (String) ((Map) transactionList.get(0)).get("id");
        with()
                .get("/transaction/info/" + transactionId)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("$", Matchers.allOf(
                        Matchers.hasEntry("id", transactionId),
                        Matchers.hasKey("sourceAccount"),
                        Matchers.hasKey("destinationAccount"),
                        Matchers.hasKey("amount"),
                        Matchers.hasKey("currency"),
                        Matchers.hasKey("comment"),
                        Matchers.hasKey("status"),
                        Matchers.hasKey("creationDateTime")
                ));
    }

    /**
     *  Negative case - non-existing transaction
     */
    @Test
    public void testGetTransactionFail() {
        final String transactionId = "no-such-id";
        with()
                .get("/transaction/info/" + transactionId)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .statusLine("HTTP/1.1 404 No transaction found with ID: " + transactionId);
    }

}
