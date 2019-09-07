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

    @Pact(provider = "EconomicControl", consumer = "PlayerHiring")
    fun createPactForDecimalAmount(builder: PactDslWithProvider): RequestResponsePact {
        val body: DslPart = PactDslJsonBody()
                .decimalType("allowed_amount", 123.45)
        return builder
                .given("Get allowed amount with decimals for an existing club")
                .uponReceiving("a request to get allowed amount to an existing club")
                .path("/balance/niupi")
                .method("get")
                .willRespondWith()
                .status(200)
                .body(body)
                .toPact()
    }

    @Test
    @PactTestFor(pactMethod = "createPactForDecimalAmount")
    fun testGetAllowedAmountWithDecimals(mockServer: MockServer) {
        // Act
        var response: HttpResponse = Request.Get(mockServer.getUrl() + "/balance/niupi").execute().returnResponse()

        // Assert
        Assertions.assertEquals(200, response.statusLine.statusCode)
        val jsonContent = JSONObject(EntityUtils.toString(response.entity))
        Assertions.assertEquals(123.45, jsonContent.get("allowed_amount"))
    }
}