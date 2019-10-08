package player.hiring

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import javax.json.Json

@Controller("/player-hiring")
class PlayerHiringController {

    @Post("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun create(): HttpResponse<Any?> {
        return  HttpResponse.ok(
            Json.createObjectBuilder()
                .add("message", "It's a test")
                .build()
                .toString()
        )
    }
}
