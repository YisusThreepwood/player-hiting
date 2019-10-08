package player.hiring

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.DslPart
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.apache.http.client.fluent.Request
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import org.json.JSONObject
import org.junit.jupiter.api.Assertions

@ExtendWith(PactConsumerTestExt::class)
class PlayerHiringPactTest {

    private val UID_REGEXP: String = "\\w*\$"
    private val DATE_REGEXP: String = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))"

    @Pact(provider = "EconomicControl", consumer = "PlayerHiring")
    fun createPactForBalance(builder: PactDslWithProvider): RequestResponsePact {
        val body: PactDslJsonBody = PactDslJsonBody()
                .decimalType("allowed_amount", 123.45)

        return builder
                .given("Get allowed amount with decimals for an existing club")
                .uponReceiving("a request to get allowed amount to an existing club")
                .path("/balance/niupi")
                .method("get")
                .headers("Content-Type", "application/json")
                .willRespondWith()
                .status(200)
                .headers(mapOf("Content-Type" to "application/json"))
                .body(body)
                .toPact()
    }

    @Test
    @PactTestFor(pactMethod = "createPactForBalance")
    fun testGetAllowedAmountWithDecimals(mockServer: MockServer) {
        // Act
        val response: HttpResponse = Request.Get(mockServer.getUrl() + "/balance/niupi")
            .addHeader("Content-Type", "application/json")
            .execute()
            .returnResponse()

        // Assert
        Assertions.assertEquals(200, response.statusLine.statusCode)
        val jsonContent = JSONObject(EntityUtils.toString(response.entity))
        Assertions.assertEquals(123.45, jsonContent.get("allowed_amount"))
    }

    @Pact(provider = "Contracts", consumer = "PlayerHiring")
    fun createPactForPlayerCurrentContract(builder: PactDslWithProvider): RequestResponsePact {
        val body: DslPart = PactDslJsonBody()
                .stringMatcher("id", UID_REGEXP, "abc123")
                .stringMatcher("player_id", UID_REGEXP, "000321")
                .stringMatcher("club_id", UID_REGEXP, "21")
                .stringMatcher("end_date", DATE_REGEXP, "1980-06-08")

        return builder
                .given("Get player's current contract")
                .uponReceiving("a request to get the info of an existing player's current contract")
                .method("get")
                .path("/contract/current/000321")
                .headers("Content-Type", "application/json")
                .willRespondWith()
                .status(200)
                .headers(mapOf("Content-Type" to "application/json"))
                .body(body)
                .toPact()
    }

    @Test
    @PactTestFor(pactMethod = "createPactForPlayerCurrentContract")
    fun testGetPlayerCurrentContract(mockServer: MockServer) {
        // Act
        val response: HttpResponse = Request.Get(mockServer.getUrl() + "/contract/current/000321")
                .addHeader("Content-Type", "application/json")
                .execute()
                .returnResponse()

        // Assert
        Assertions.assertEquals(200, response.statusLine.statusCode)
        Assertions.assertEquals("application/json", response.getHeaders("Content-Type").last().value);

        val jsonContent = JSONObject(EntityUtils.toString(response.entity))
        Assertions.assertEquals("abc123", jsonContent.get("id"))
        Assertions.assertEquals("000321", jsonContent.get("player_id"))
        Assertions.assertEquals("21", jsonContent.get("club_id"))
        Assertions.assertEquals("1980-06-08", jsonContent.get("end_date"))
    }
}
